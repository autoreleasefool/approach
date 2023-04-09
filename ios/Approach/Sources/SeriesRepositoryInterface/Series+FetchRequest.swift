import ModelsLibrary

extension Series {
	public struct FetchRequest: Equatable {
		public let filter: Filter
		public let ordering: Ordering

		public init(filter: Filter, ordering: Ordering) {
			self.filter = filter
			self.ordering = ordering
		}
	}
}

extension Series.FetchRequest {
	public struct Filter: Equatable {
		public let league: League.ID

		public init(
			league: League.ID
		) {
			self.league = league
		}
	}
}

extension Series.FetchRequest {
	public enum Ordering: Hashable, CaseIterable {
		case byDate
	}
}
