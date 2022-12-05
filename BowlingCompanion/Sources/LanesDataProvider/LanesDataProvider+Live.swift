import Dependencies
import Foundation
import LanesDataProviderInterface
import PersistenceServiceInterface
import SharedModelsLibrary

extension LanesDataProvider: DependencyKey {
	public static let liveValue = Self(
		fetchLanes: { request in
			@Dependency(\.persistenceService) var persistenceService: PersistenceService

			switch request.ordering {
			case .byLabel:
				return try await persistenceService.fetchLanes(.init(request))
			}
		}
	)
}

extension Lane.Query {
	init(_ request: Lane.FetchRequest) {
		let filter: [Lane.Query.Filter] = request.filter.map {
			switch $0 {
			case let .id(id): return .id(id)
			case let .alley(alley): return .alley(alley)
			}
		}

		let ordering: Lane.Query.Ordering
		switch request.ordering {
		case .byLabel:
			ordering = .byLabel
		}

		self.init(filter: filter, ordering: ordering)
	}
}
