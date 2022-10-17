import Dependencies
import ExtensionsLibrary
import SeriesDataProviderInterface
import PersistenceModelsLibrary
import PersistenceServiceInterface
import RealmSwift
import SharedModelsLibrary

extension SeriesDataProvider: DependencyKey {
	public static let liveValue: Self = {
		@Dependency(\.persistenceService) var persistenceService: PersistenceService

		return Self(
			create: { league, series in
				try await withCheckedThrowingContinuation { (continuation: CheckedContinuation<Void, Error>) in
					persistenceService.write({
						let persistent = PersistentSeries(from: series)
						$0.add(persistent, update: .error)
						$0.object(ofType: PersistentLeague.self, forPrimaryKey: league.id)?.series
							.append(persistent)
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
