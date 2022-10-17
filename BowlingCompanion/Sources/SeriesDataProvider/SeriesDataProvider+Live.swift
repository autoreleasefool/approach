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
						let persistentSeries = PersistentSeries(from: series)
						$0.add(persistentSeries, update: .error)
						$0.object(ofType: PersistentLeague.self, forPrimaryKey: league.id)?.series
							.append(persistentSeries)

						// TODO: try to consolidate with LeaguesDataProvider.create
						for ordinal in 1...league.numberOfGames {
							let game = PersistentGame(from: .init(id: uuid(), ordinal: ordinal, locked: .unlocked, manualScore: nil))
							$0.add(game, update: .error)
							persistentSeries.games.append(game)
						}
					}, continuation.resumeOrThrow(_:))
				}
			},
			delete: { series in
				try await withCheckedThrowingContinuation { (continuation: CheckedContinuation<Void, Error>) in
					persistenceService.write({
						if let persistent = $0.object(ofType: PersistentSeries.self, forPrimaryKey: series.id) {
							$0.delete(persistent)
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
