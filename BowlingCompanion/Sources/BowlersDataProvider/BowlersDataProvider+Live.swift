import Dependencies
import BowlersDataProviderInterface
import PersistenceModelsLibrary
import PersistenceServiceInterface
import SharedModelsLibrary

extension BowlersDataProvider: DependencyKey {
	public static let liveValue: Self = {
		@Sendable func resumeOrThrow(_ error: Error?, continuation: CheckedContinuation<Void, Error>) {
			if let error {
				continuation.resume(throwing: error)
			} else {
				continuation.resume(returning: ())
			}
		}

		@Dependency(\.persistenceService) var persistenceService: PersistenceService

		return Self(
			save: { bowler in
				try await withCheckedThrowingContinuation { (continuation: CheckedContinuation<Void, Error>) in
					persistenceService.write({ realm in
						realm.add(PersistentBowler(from: bowler))
					}, {
						resumeOrThrow($0, continuation: continuation)
					})
				}
			},
			delete: { bowler in
				try await withCheckedThrowingContinuation { (continuation: CheckedContinuation<Void, Error>) in
					persistenceService.write({ realm in
						if let persistent = realm.object(ofType: PersistentBowler.self, forPrimaryKey: bowler.id) {
							realm.delete(persistent)
						}
					}, {
						resumeOrThrow($0, continuation: continuation)
					})
				}
			},
			fetchAll: {
				.init { continuation in
					persistenceService.read {
						let bowlers = $0.objects(PersistentBowler.self)
						let token = bowlers.observe { _ in
							// TODO: capture and send errors
							continuation.yield(bowlers.map { $0.bowler })
						}

						continuation.onTermination = { _ in
							token.invalidate()
						}
					}
				}
			}
		)
	}()
}
