import AchievementsLibrary
@testable import AchievementsService
@testable import AchievementsServiceInterface
import DatabaseServiceInterface
import Dependencies
import FeatureFlagsPackageServiceInterface
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
				$0[DatabaseService.self].writer = { @Sendable in db }
				$0[AchievementsService.self] = .liveValue
			} operation: {
				await achievements.sendEvent(EarnableAchievements.Iconista.Events.AppIconsViewed())
			}

			// Does not insert any records
			let eventsCount = try await db.read { try AchievementEvent.Database.fetchCount($0) }
			#expect(eventsCount == 0)
		}

		@Test("Sends event when feature flag is enabled", .tags(.unit))
		func sendsEvent_whenFeatureFlagIsEnabled() async throws {
			// Given an empty database
			let db = try initializeApproachDatabase(withAchievementEvents: .zero, withAchievements: .zero)

			// Sending a valid event
			await withDependencies {
				$0.featureFlags.isEnabled = { $0 == .achievements }
				$0.uuid = .incrementing
				$0[DatabaseService.self].writer = { @Sendable in db }
				$0[AchievementsService.self] = .liveValue
			} operation: {
				await achievements.sendEvent(EarnableAchievements.Iconista.Events.AppIconsViewed())
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
				$0[DatabaseService.self].writer = { @Sendable in db }
				$0[AchievementsService.self] = .liveValue
			} operation: {
				await achievements.sendEvent(InvalidEvent())
			}

			// Does not insert any records
			let eventsCount = try await db.read { try AchievementEvent.Database.fetchCount($0) }
			#expect(eventsCount == 0)
		}
	}
}

struct InvalidEvent: ConsumableAchievementEvent {
	static var title: String { "InvalidEvent" }
}
