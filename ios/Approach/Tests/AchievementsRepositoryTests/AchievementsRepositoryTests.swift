@testable import AchievementsRepository
@testable import AchievementsRepositoryInterface
import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import DependenciesTestSupport
import FeatureFlagsPackageLibrary
import Foundation
import GRDB
@testable import ModelsLibrary
import TestDatabaseUtilitiesLibrary
import Testing
import TestUtilitiesLibrary
import TestUtilitiesPackageLibrary

@Suite("AchievementsRepository", .tags(.repository))
struct AchievementsRepositoryTests {

	// MARK: list

	@Suite("list", .tags(.dependencies, .grdb))
	struct ListTests {
		@Dependency(AchievementsRepository.self) var achievements

		@Test("Lists all achievements", .tags(.unit))
		func listAllAchievements() async throws {
			// Given a database with achievements
			let achievement1 = Achievement.Database.mock(id: UUID(0), title: "Ten Years", earnedAt: Date(timeIntervalSince1970: 123))
			let achievement2 = Achievement.Database.mock(id: UUID(1), title: "Iconista", earnedAt: Date(timeIntervalSince1970: 456))
			let db = try initializeApproachDatabase(withAchievements: .custom([achievement1, achievement2]))

			// Listing the achievements
			let achievementsList = withDependencies {
				$0.featureFlags.isEnabled = { $0 == .achievements }
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[AchievementsRepository.self] = .liveValue
			} operation: {
				achievements.list()
			}

			var iterator = achievementsList.makeAsyncIterator()
			let fetched = try await iterator.next()

			let expectedAchievements = [
				Achievement.List(title: "Ten Years", firstEarnedAt: Date(timeIntervalSince1970: 123), count: 1),
				Achievement.List(title: "Iconista", firstEarnedAt: Date(timeIntervalSince1970: 456), count: 1),
			]

			// Returns all the achievements
			#expect(fetched == expectedAchievements)
		}

		@Test("Correctly counts achievements earned multiple times", .tags(.unit))
		func correctlyCountsAchievementsEarnedMultipleTimes() async throws {
			// Given a database with achievements
			let achievement1 = Achievement.Database.mock(id: UUID(0), title: "Ten Years", earnedAt: Date(timeIntervalSince1970: 123))
			let achievement2 = Achievement.Database.mock(id: UUID(1), title: "Ten Years", earnedAt: Date(timeIntervalSince1970: 456))
			let achievement3 = Achievement.Database.mock(id: UUID(2), title: "Ten Years", earnedAt: Date(timeIntervalSince1970: 789))
			let db = try initializeApproachDatabase(withAchievements: .custom([achievement1, achievement2, achievement3]))

			// Listing the achievements
			let achievementsList = withDependencies {
				$0.featureFlags.isEnabled = { $0 == .achievements }
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[AchievementsRepository.self] = .liveValue
			} operation: {
				achievements.list()
			}

			var iterator = achievementsList.makeAsyncIterator()
			let fetched = try await iterator.next()

			let expectedAchievements = [
				Achievement.List(title: "Ten Years", firstEarnedAt: Date(timeIntervalSince1970: 123), count: 3),
			]

			// Returns all the achievements
			#expect(fetched == expectedAchievements)
		}

		@Test("Returns correct firstEarnedDate for achievements earned multiple times", .tags(.unit))
		func correctFirstEarnedDate_forAchievementsEarnedMultipleTimes() async throws {
			// Given a database with achievements
			let achievement1 = Achievement.Database.mock(id: UUID(0), title: "Ten Years", earnedAt: Date(timeIntervalSince1970: 456))
			let achievement2 = Achievement.Database.mock(id: UUID(1), title: "Ten Years", earnedAt: Date(timeIntervalSince1970: 123))
			let achievement3 = Achievement.Database.mock(id: UUID(2), title: "Ten Years", earnedAt: Date(timeIntervalSince1970: 789))
			let db = try initializeApproachDatabase(withAchievements: .custom([achievement1, achievement2, achievement3]))

			// Listing the achievements
			let achievementsList = withDependencies {
				$0.featureFlags.isEnabled = { $0 == .achievements }
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[AchievementsRepository.self] = .liveValue
			} operation: {
				achievements.list()
			}

			var iterator = achievementsList.makeAsyncIterator()
			let fetched = try await iterator.next()

			let expectedAchievements = [
				Achievement.List(title: "Ten Years", firstEarnedAt: Date(timeIntervalSince1970: 123), count: 3),
			]

			// Returns all the achievements
			#expect(fetched == expectedAchievements)
		}
	}

	// MARK: observeNewAchievements

	@Suite("observeNewAchievements", .tags(.dependencies, .grdb))
	struct ObserveNewAchievementsTests {
		@Dependency(AchievementsRepository.self) var achievements

		@Test("Finishes immediately when feature flag is disabled", .tags(.unit))
		func finishesImmediately_whenFeatureFlagIsDisabled() async throws {
			await withDependencies {
				$0.featureFlags.isEnabled = { _ in false }
				$0[AchievementsRepository.self] = .liveValue
			} operation: {
				await confirmation { finishes in
					let task = Task {
						for await _ in achievements.observeNewAchievements() { }
						finishes()
					}

					await task.value
				}
			}
		}

		@Test("Sends new achievements", .tags(.unit))
		func sendsNewAchievements() async throws {
			// Given an empty database
			let db = try initializeApproachDatabase(withAchievementEvents: .zero, withAchievements: .zero)

			try await withDependencies {
				$0.featureFlags.isEnabled = { _ in true }
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[AchievementsRepository.self] = .liveValue
				$0.date = .constant(Date(timeIntervalSince1970: 122))
			} operation: {
				let achievement1 = Achievement.Database.mock(id: UUID(0), title: "Ten Years", earnedAt: Date(timeIntervalSince1970: 123))
				let achievement2 = Achievement.Database.mock(id: UUID(1), title: "Ten Years", earnedAt: Date(timeIntervalSince1970: 456))
				let expectedAchievements = [Achievement.Summary(achievement1), Achievement.Summary(achievement2)]

				try await confirmation(expectedCount: 2) { receivesAchievement in
					let task = Task {
						var received: [Achievement.Summary] = []

						// Expect 2 achievements to be received
						for await achievement in achievements.observeNewAchievements() {
							#expect(expectedAchievements.contains(achievement))
							receivesAchievement()

							received.append(achievement)
							if received.count == 2 {
								break
							}
						}
					}

					try await db.write { try achievement1.insert($0) }
					try await db.write { try achievement2.insert($0) }

					await task.value
				}
			}
		}

		@Test("Does not report existing achievements", .tags(.unit))
		func doesNotReportExistingAchievements() async throws {
			// Given a database with existing achievements
			let achievement1 = Achievement.Database.mock(id: UUID(0), title: "Ten Years", earnedAt: Date(timeIntervalSince1970: 123))
			let achievement2 = Achievement.Database.mock(id: UUID(1), title: "Ten Years", earnedAt: Date(timeIntervalSince1970: 456))
			let expectedAchievements = [Achievement.Summary(achievement2)]

			let db = try initializeApproachDatabase(withAchievements: .custom([achievement1]))

			try await withDependencies {
				$0.featureFlags.isEnabled = { _ in true }
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[AchievementsRepository.self] = .liveValue
				$0.date = .constant(Date(timeIntervalSince1970: 124))
			} operation: {
				try await confirmation { receivesAchievement in
					let task = Task {
						var received: [Achievement.Summary] = []

						// Expect 1 achievement to be received
						for await achievement in achievements.observeNewAchievements() {
							#expect(expectedAchievements.contains(achievement))
							receivesAchievement()

							received.append(achievement)
							if received.count == 1 {
								break
							}
						}
					}

					try await db.write { try achievement2.insert($0) }

					await task.value
				}
			}
		}
	}
}
