import AchievementsLibrary
@testable import AchievementsService
@testable import AchievementsServiceInterface
import DatabaseServiceInterface
import Dependencies
import FeatureFlagsPackageServiceInterface
import Foundation
import ModelsLibrary
import TestDatabaseUtilitiesLibrary
import Testing
import TestUtilitiesLibrary
import TestUtilitiesPackageLibrary

@Suite("AchievementsService", .tags(.service))
struct AchievementsServiceTests {

	@Suite("sendEvent", .tags(.dependencies, .grdb))
	struct SendEventTests {
		@Dependency(AchievementsService.self) var achievements

		@Test("Does nothing when feature flag is disabled", .tags(.unit))
		func doesNothing_whenFeatureFlagIsDisabled() async throws {
			// Given an empty database
			let db = try initializeApproachDatabase(withAchievementEvents: .zero, withAchievements: .zero)

			// Sending a valid event
			await withDependencies {
				$0.featureFlags.isEnabled = { $0 != .achievements }
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[DatabaseService.self].writer = { @Sendable in db }
				$0[AchievementsService.self] = .liveValue
			} operation: {
				await achievements.sendEvent(EarnableAchievements.Iconista.Events.AppIconsViewed(id: UUID(0)))
			}

			// Does not insert any records
			let eventsCount = try await db.read { try AchievementEvent.Database.fetchCount($0) }
			#expect(eventsCount == 0)

			// Does not insert any achievements
			let achievementsCount = try await db.read { try Achievement.Database.fetchCount($0) }
			#expect(achievementsCount == 0)
		}

		@Test("Sends event when feature flag is enabled", .tags(.unit))
		func sendsEvent_whenFeatureFlagIsEnabled() async throws {
			// Given an empty database
			let db = try initializeApproachDatabase(withAchievementEvents: .zero, withAchievements: .zero)

			// Sending a valid event
			await withDependencies {
				$0.featureFlags.isEnabled = { $0 == .achievements }
				$0.uuid = .incrementing
				$0.date = .constant(Date(timeIntervalSince1970: 123))
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[DatabaseService.self].writer = { @Sendable in db }
				$0[AchievementsService.self] = .liveValue
			} operation: {
				await achievements.sendEvent(EarnableAchievements.Iconista.Events.AppIconsViewed(id: UUID(0)))
			}

			// Inserts a record
			let eventsCount = try await db.read { try AchievementEvent.Database.fetchCount($0) }
			#expect(eventsCount == 1)
		}

		@Test("Does nothing when event is invalid", .tags(.unit))
		func doesNothing_whenEventIsInvalid() async throws {
			// Given an empty database
			let db = try initializeApproachDatabase(withAchievementEvents: .zero, withAchievements: .zero)

			// Sending an invalid event
			await withDependencies {
				$0.featureFlags.isEnabled = { $0 == .achievements }
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[DatabaseService.self].writer = { @Sendable in db }
				$0[AchievementsService.self] = .liveValue
			} operation: {
				await achievements.sendEvent(InvalidEvent())
			}

			// Does not insert any records
			let eventsCount = try await db.read { try AchievementEvent.Database.fetchCount($0) }
			#expect(eventsCount == 0)
		}

		@Test("Sending an event consumes it", .tags(.unit))
		func sendingAnEvent_consumeIt() async throws {
			// Given an empty database
			let db = try initializeApproachDatabase(withAchievementEvents: .zero, withAchievements: .zero)

			// Sending a valid event
			await withDependencies {
				$0.featureFlags.isEnabled = { $0 == .achievements }
				$0.uuid = .incrementing
				$0.date = .constant(Date(timeIntervalSince1970: 123))
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[DatabaseService.self].writer = { @Sendable in db }
				$0[AchievementsService.self] = .liveValue
			} operation: {
				await achievements.sendEvent(EarnableAchievements.Iconista.Events.AppIconsViewed(id: UUID(0)))
			}

			// Consumes the event
			let inserted = try await db.read { try AchievementEvent.Database.fetchOne($0, id: UUID(0)) }
			#expect(inserted?.isConsumed == true)

			// Creates an achievement
			let achievement = try await db.read { try Achievement.Database.fetchOne($0) }
			#expect(achievement != nil)
			#expect(achievement?.title == "Iconista")
		}
	}
}

struct InvalidEvent: ConsumableAchievementEvent {
	static var title: String { "InvalidEvent" }
	var id: UUID = UUID()
}
