import Dependencies
import PersistenceServiceInterface
import SeriesDataProviderInterface

extension SeriesDataProvider: DependencyKey {
	public static var liveValue: Self = {
		@Dependency(\.persistenceService) var persistenceService: PersistenceService

		return .init(
			observeSeries: { request in
				switch request.ordering {
				case .byDate:
					return persistenceService.observeSeries(request)
				}
			}
		)
	}()
}
