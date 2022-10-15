import ComposableArchitecture
import SeriesDataProviderInterface
import PersistenceModelsLibrary
import PersistenceServiceInterface
import RealmSwift
import SharedModelsLibrary

extension SeriesDataProvider: DependencyKey {
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
			save: { league, series in
				try await withCheckedThrowingContinuation { (continuation: CheckedContinuation<Void, Error>) in
					persistenceService.write({
						$0.object(ofType: PersistentLeague.self, forPrimaryKey: league.id)?
							.series.append(PersistentSeries(from: series))
					}, {
						resumeOrThrow($0, continuation: continuation)
					})
				}
			},
			delete: { series in
				try await withCheckedThrowingContinuation { (continuation: CheckedContinuation<Void, Error>) in
					persistenceService.write({
						if let persistent = $0.object(ofType: PersistentSeries.self, forPrimaryKey: series.id) {
							$0.delete(persistent)
						}
					}, {
						resumeOrThrow($0, continuation: continuation)
					})
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
							print("Terminating")
						}
					}
				}
			}
		)
	}()
}
