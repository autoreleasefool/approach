import Dependencies
import ExtensionsLibrary
import GamesDataProviderInterface
import PersistenceModelsLibrary
import PersistenceServiceInterface
import RealmSwift
import SharedModelsLibrary

extension GamesDataProvider: DependencyKey {
	public static let liveValue: Self = {
		@Dependency(\.persistenceService) var persistenceService: PersistenceService

		return Self(
			save: { series, game in
				try await withCheckedThrowingContinuation { (continuation: CheckedContinuation<Void, Error>) in
					persistenceService.write({
						$0.object(ofType: PersistentSeries.self, forPrimaryKey: series.id)?
							.games.append(PersistentGame(from: game))
					}, continuation.resumeOrThrow(_:))
				}
			},
			delete: { game in
				try await withCheckedThrowingContinuation { (continuation: CheckedContinuation<Void, Error>) in
					persistenceService.write({
						if let persistent = $0.object(ofType: PersistentGame.self, forPrimaryKey: game.id) {
							$0.delete(persistent)
						}
					}, continuation.resumeOrThrow(_:))
				}
			},
			fetchAll: { series in
				.init { continuation in
					persistenceService.read {
						let series = $0.object(ofType: PersistentSeries.self, forPrimaryKey: series.id)

						var token: NotificationToken?
						if let series {
							let games = series.games
							token = games.observe { _ in
								continuation.yield(games.map { $0.game })
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
