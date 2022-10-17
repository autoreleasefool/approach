import BowlersDataProviderInterface
import Dependencies
import ExtensionsLibrary
import PersistenceModelsLibrary
import PersistenceServiceInterface
import SharedModelsLibrary

extension BowlersDataProvider: DependencyKey {
	public static let liveValue: Self = {
		@Dependency(\.persistenceService) var persistenceService: PersistenceService

		return Self(
			create: { bowler in
				try await withCheckedThrowingContinuation { (continuation: CheckedContinuation<Void, Error>) in
					persistenceService.write({
						$0.add(PersistentBowler(from: bowler), update: .error)
					}, continuation.resumeOrThrow(_:))
				}
			},
			update: { bowler in
				try await withCheckedThrowingContinuation { (continuation: CheckedContinuation<Void, Error>) in
					persistenceService.write({
						$0.add(PersistentBowler(from: bowler), update: .modified)
					}, continuation.resumeOrThrow(_:))
				}
			},
			delete: { bowler in
				try await withCheckedThrowingContinuation { (continuation: CheckedContinuation<Void, Error>) in
					persistenceService.write({
						if let persistent = $0.object(ofType: PersistentBowler.self, forPrimaryKey: bowler.id) {
							$0.delete(persistent)
						}
					}, continuation.resumeOrThrow(_:))
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
