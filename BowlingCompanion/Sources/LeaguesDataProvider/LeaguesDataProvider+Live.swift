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
						let createdLeague = PersistentLeague(from: league)
						$0.add(createdLeague, update: .error)
						$0.object(ofType: PersistentBowler.self, forPrimaryKey: bowler.id)?.leagues
							.append(createdLeague)

						if league.recurrence == .oneTimeEvent {
							let createdSeries = PersistentSeries(from: .init(id: uuid(), date: date()))
							$0.add(createdSeries, update: .error)
							createdLeague.series.append(createdSeries)

							for ordinal in 1...league.numberOfGames {
								let createdGame = PersistentGame(
									from: .init(id: uuid(), ordinal: ordinal, locked: .unlocked, manualScore: nil)
								)
								$0.add(createdGame, update: .error)
								createdSeries.games.append(createdGame)
							}
						}
					}, continuation.resumeOrThrow(_:))
				}
			},
			delete: { league in
				try await withCheckedThrowingContinuation { (continuation: CheckedContinuation<Void, Error>) in
					persistenceService.write({
						if let deletedLeague = $0.object(ofType: PersistentLeague.self, forPrimaryKey: league.id) {
							for series in deletedLeague.series {
								for game in series.games {
									$0.delete(game)
								}

								$0.delete(series)
							}

							$0.delete(deletedLeague)
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
