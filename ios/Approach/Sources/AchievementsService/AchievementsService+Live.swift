import AchievementsLibrary
import AchievementsServiceInterface
import Dependencies
import FeatureFlagsLibrary

extension AchievementsService: DependencyKey {
	public static var liveValue: Self {
		let events = LockIsolated<[ConsumableAchievementEvent]>([])
		let achievements = LockIsolated<[EarnableAchievement]>([])
		let newAchievementsObservers = LockIsolated<[AsyncStream<EarnableAchievement>.Continuation]>([])

		return AchievementsService(
			observeNewAchievements: {
				@Dependency(\.featureFlags) var featureFlags
				guard featureFlags.isFlagEnabled(.achievements) else { return .finished }
				let (stream, continuation) = AsyncStream<EarnableAchievement>.makeStream()
				newAchievementsObservers.withValue { $0.append(continuation) }
				return stream
			},
			allEarnedAchievements: {
				@Dependency(\.featureFlags) var featureFlags
				guard featureFlags.isFlagEnabled(.achievements) else { return .finished() }

				return .finished()
			},
			sendEvent: { event in
				@Dependency(\.featureFlags) var featureFlags
				guard featureFlags.isFlagEnabled(.achievements) else { return }

				let earned = events.withValue { events in
					events.append(event)

					return EarnableAchievements.allCases.flatMap {
						$0.consume(from: &events)
					}
				}

				achievements.withValue { $0.append(contentsOf: earned) }

				for earned in earned {
					newAchievementsObservers.value.forEach { $0.yield(earned) }
				}
			}
		)
	}
}
