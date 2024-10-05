import BadgesLibrary
import BadgesServiceInterface
import Dependencies
import FeatureFlagsLibrary

extension BadgesService: DependencyKey {
	public static var liveValue: Self {
		let events = LockIsolated<[ConsumableBadgeEvent]>([])
		let badges = LockIsolated<[EarnableBadge]>([])
		let newBadgesObservers = LockIsolated<[AsyncStream<EarnableBadge>.Continuation]>([])

		return BadgesService(
			observeNewBadges: {
				@Dependency(\.featureFlags) var featureFlags
				guard featureFlags.isFlagEnabled(.badges) else { return .finished }
				let (stream, continuation) = AsyncStream<EarnableBadge>.makeStream()
				newBadgesObservers.withValue { $0.append(continuation) }
				return stream
			},
			allEarnedBadges: {
				@Dependency(\.featureFlags) var featureFlags
				guard featureFlags.isFlagEnabled(.badges) else { return .finished() }

				return .finished()
			},
			sendEvent: { event in
				@Dependency(\.featureFlags) var featureFlags
				guard featureFlags.isFlagEnabled(.badges) else { return }

				let earned = events.withValue { events in
					events.append(event)

					return EarnableBadges.allCases.flatMap {
						$0.consume(from: &events)
					}
				}

				badges.withValue { $0.append(contentsOf: earned) }

				for earned in earned {
					newBadgesObservers.value.forEach { $0.yield(earned) }
				}
			}
		)
	}
}
