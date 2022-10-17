import Dependencies
import ExtensionsLibrary
import LeaguesDataProviderInterface
import PersistenceModelsLibrary
import PersistenceServiceInterface
import RealmSwift
import SharedModelsLibrary

extension LeaguesDataProvider: DependencyKey {
	public static let liveValue: Self = {
		@Dependency(\.uuid) var uuid
		@Dependency(\.date) var date
		@Dependency(\.persistenceService) var persistenceService: PersistenceService

		return Self(
			create: { bowler, league in
				try await withCheckedThrowingContinuation { (continuation: CheckedContinuation<Void, Error>) in
					persistenceService.write({
						let persistentLeague = PersistentLeague(from: league)
						$0.add(persistentLeague, update: .error)
						$0.object(ofType: PersistentBowler.self, forPrimaryKey: bowler.id)?.leagues
							.append(persistentLeague)

						if league.recurrence == .oneTimeEvent {
							let series = PersistentSeries(from: .init(id: uuid(), date: date()))
							$0.add(series, update: .error)
							persistentLeague.series.append(series)
							for ordinal in 1...league.numberOfGames {
								let game = PersistentGame(
									from: .init(id: uuid(), ordinal: ordinal, locked: .unlocked, manualScore: nil)
								)
								$0.add(game, update: .error)
								series.games.append(game)
							}
						}
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
