import Dependencies
import Foundation
import SeriesDataProviderInterface
import PersistenceServiceInterface
import SharedModelsLibrary

extension SeriesDataProvider: DependencyKey {
	public static let liveValue = Self(
		fetchSeries: { request in
			@Dependency(\.persistenceService) var persistenceService: PersistenceService

			switch request.ordering {
			case .byDate:
				return try await persistenceService.fetchSeries(.init(request))
			}
		}
	)
}

extension Series.Query {
	init(_ request: Series.FetchRequest) {
		let ordering: Series.Query.Ordering
		switch request.ordering {
		case .byDate:
			ordering = .byDate
		}

		self.init(league: request.league, ordering: ordering)
	}
}
