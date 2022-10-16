import Dependencies
import LeaguesDataProviderInterface
import PersistenceModelsLibrary
import PersistenceServiceInterface
import RealmSwift
import SharedModelsLibrary

extension LeaguesDataProvider: DependencyKey {
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
			save: { bowler, league in
				try await withCheckedThrowingContinuation { (continuation: CheckedContinuation<Void, Error>) in
					persistenceService.write({
						$0.object(ofType: PersistentBowler.self, forPrimaryKey: bowler.id)?
							.leagues.append(PersistentLeague(from: league))
					}, {
						resumeOrThrow($0, continuation: continuation)
					})
				}
			},
			delete: { league in
				try await withCheckedThrowingContinuation { (continuation: CheckedContinuation<Void, Error>) in
					persistenceService.write({
						if let persistent = $0.object(ofType: PersistentLeague.self, forPrimaryKey: league.id) {
							$0.delete(persistent)
						}
					}, {
						resumeOrThrow($0, continuation: continuation)
					})
				}
			},
			fetchAll: { bowler in
				.init { continuation in
					persistenceService.read {
						let bowler = $0.object(ofType: PersistentBowler.self, forPrimaryKey: bowler.id)

						var token: NotificationToken?
						if let bowler {
							let leagues = bowler.leagues
							token = leagues.observe { _ in
								continuation.yield(leagues.map { $0.league })
							}
						} else {
							continuation.finish()
						}

						continuation.onTermination = { [token = token] _ in
							token?.invalidate()
						}
					}
				}
			}
		)
	}()
}
