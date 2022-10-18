import Dependencies
import ExtensionsLibrary
import SeriesDataProviderInterface
import PersistenceModelsLibrary
import PersistenceServiceInterface
import RealmSwift
import SharedModelsLibrary

extension SeriesDataProvider: DependencyKey {
	public static let liveValue: Self = {
		@Dependency(\.uuid) var uuid
		@Dependency(\.persistenceService) var persistenceService

		return Self(
			create: { league, series in
				try await withCheckedThrowingContinuation { (continuation: CheckedContinuation<Void, Error>) in
					persistenceService.write({
						let createdSeries = PersistentSeries(from: series)
						$0.add(createdSeries, update: .error)
						$0.object(ofType: PersistentLeague.self, forPrimaryKey: league.id)?.series
							.append(createdSeries)

						for ordinal in 1...league.numberOfGames {
							let createdGame = PersistentGame(
								from: .init(id: uuid(), ordinal: ordinal)
							)
							$0.add(createdGame, update: .error)
							createdSeries.games.append(createdGame)
						}
					}, continuation.resumeOrThrow(_:))
				}
			},
			delete: { series in
				try await withCheckedThrowingContinuation { (continuation: CheckedContinuation<Void, Error>) in
					persistenceService.write({
						if let deletedSeries = $0.object(ofType: PersistentSeries.self, forPrimaryKey: series.id) {
							for game in deletedSeries.games {
								$0.delete(game)
							}

							$0.delete(deletedSeries)
						}
					}, continuation.resumeOrThrow(_:))
				}
			},
			fetchAll: { league in
				.init { continuation in
					persistenceService.read {
						let league = $0.object(ofType: PersistentLeague.self, forPrimaryKey: league.id)

						var token: NotificationToken?
						if let league {
							let series = league.series
							token = series.observe { _ in
								continuation.yield(series.map { $0.series })
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
