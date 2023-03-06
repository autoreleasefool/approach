import Dependencies
import SeriesDataProviderInterface
import PersistenceServiceInterface

extension SeriesDataProvider: DependencyKey {
	public static let liveValue: Self = {
		@Dependency(\.persistenceService) var persistenceService: PersistenceService

		return .init(
			fetchSeries: { request in
				let series = try await persistenceService.fetchSeries(request)

				switch request.ordering {
				case .byDate:
					return series
				}
			},
			observeSeries: { request in
				switch request.ordering {
				case .byDate:
					return persistenceService.observeSeries(request)
				}
			}
		)
	}()
}
