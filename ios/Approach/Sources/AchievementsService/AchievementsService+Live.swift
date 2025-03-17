import AchievementsLibrary
import AchievementsServiceInterface
import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import ErrorReportingClientPackageLibrary
import FeatureFlagsLibrary
import ModelsLibrary

extension AchievementsService: DependencyKey {
	public static var liveValue: Self {
		AchievementsService(
			sendEvent: { event in
				@Dependency(\.featureFlags) var featureFlags
				guard featureFlags.isFlagEnabled(.achievements) else { return }

				@Dependency(\.uuid) var uuid
				@Dependency(DatabaseService.self) var database

				let isValidEvent = EarnableAchievements
					.allCases
					.contains(where: { $0.events.contains(where: { type(of: event) == $0 }) })

				guard isValidEvent else { return }

				do {
					let dbEvent = AchievementEvent.Database(id: uuid(), title: event.title, isConsumed: false)
					try await database.writer().write {
						try dbEvent.save($0)
					}
				} catch {
					@Dependency(\.errors) var errors
					errors.captureError(error)
				}
			}
		)
	}
}
