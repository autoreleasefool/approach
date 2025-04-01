import AchievementsLibrary
import AchievementsServiceInterface
import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import ErrorReportingClientPackageLibrary
import FeatureFlagsLibrary
import GRDB
import ModelsLibrary

extension AchievementsService: DependencyKey {
	public static var liveValue: Self {
		let achievementTracker = EarnedAchievementTracker()

		return AchievementsService(
			sendEvent: { event in
				@Dependency(\.featureFlags) var featureFlags
				guard featureFlags.isFlagEnabled(.achievements) else { return }

				await achievementTracker.sendEvent(event)
			},
			hasEarnedAchievement: { achievementType in
				@Dependency(DatabaseService.self) var database

				do {
					return try database.reader().read {
						// GRDB does not expose a `contains` predicate for us in this case
						// swiftlint:disable:next contains_over_filter_is_empty
						try Achievement.Database
							.filter(Achievement.Database.Columns.title == achievementType.title)
							.isEmpty($0)
					} == false
				} catch {
					@Dependency(\.errors) var errors
					errors.captureError(error)
					return false
				}
			}
		)
	}
}

private actor EarnedAchievementTracker {
	func sendEvent(_ event: ConsumableAchievementEvent) {
		@Dependency(\.uuid) var uuid
		@Dependency(\.date) var date
		@Dependency(DatabaseService.self) var database

		guard let achievement = EarnableAchievements
			.allCases
			.first(where: { $0.events.contains { type(of: event) == $0 } }) else {
			return
		}

		guard achievement.isEnabled else { return }

		let relevantEvents = achievement.events.map { $0.title }

		do {
			let newEvent = AchievementEvent.Database(id: event.id, title: type(of: event).title, isConsumed: false)
			let unconsumedDbEvents = try database.reader().read {
				try AchievementEvent.Database
					.all()
					.filter(AchievementEvent.Database.Columns.isConsumed == false)
					.filter(relevantEvents.contains(AchievementEvent.Database.Columns.title))
					.fetchAll($0)
			} + [newEvent]

			let consumable = unconsumedDbEvents
				.compactMap { achievement.eventsByTitle[$0.title]?.init(id: $0.id) }

			let (consumed, earned) = achievement.consume(from: consumable)

			let earnedAchievements = earned
				.map { Achievement.Database(id: uuid(), title: type(of: $0).title, earnedAt: date()) }

			_ = try database.writer().write {
				try newEvent.insert($0)

				guard !earnedAchievements.isEmpty else {
					return
				}

				try AchievementEvent.Database
					.filter(consumed.contains(AchievementEvent.Database.Columns.id))
					.updateAll($0, AchievementEvent.Database.Columns.isConsumed.set(to: true))

				for achievement in earnedAchievements {
					try achievement.insert($0)
				}
			}
		} catch {
			@Dependency(\.errors) var errors
			errors.captureError(error)
		}
	}
}
