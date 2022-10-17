import Dependencies
import ExtensionsLibrary
import LeaguesDataProviderInterface
import PersistenceModelsLibrary
import PersistenceServiceInterface
import RealmSwift
import SharedModelsLibrary

extension LeaguesDataProvider: DependencyKey {
	public static let liveValue: Self = {
		@Dependency(\.persistenceService) var persistenceService: PersistenceService

		return Self(
			create: { bowler, league in
				try await withCheckedThrowingContinuation { (continuation: CheckedContinuation<Void, Error>) in
					persistenceService.write({
						let persistent = PersistentLeague(from: league)
						$0.add(persistent, update: .error)
						$0.object(ofType: PersistentBowler.self, forPrimaryKey: bowler.id)?.leagues
							.append(persistent)
					}, continuation.resumeOrThrow(_:))
				}
			},
			delete: { league in
				try await withCheckedThrowingContinuation { (continuation: CheckedContinuation<Void, Error>) in
					persistenceService.write({
						if let persistent = $0.object(ofType: PersistentLeague.self, forPrimaryKey: league.id) {
							$0.delete(persistent)
						}
					}, continuation.resumeOrThrow(_:))
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
