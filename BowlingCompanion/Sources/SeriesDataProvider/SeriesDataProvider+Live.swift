import Dependencies
import SeriesDataProviderInterface
import PersistenceServiceInterface

extension SeriesDataProvider: DependencyKey {
	public static let liveValue = Self(
		fetchSeries: { request in
			@Dependency(\.persistenceService) var persistenceService: PersistenceService
			let series = try await persistenceService.fetchSeries(request)

			switch request.ordering {
			case .byDate:
				return series
			}
		}
	)
}
