import SharedModelsLibrary

extension Series {
	public struct FetchRequest: Equatable {
		public let filter: Filter?
		public let ordering: Ordering

		public init(filter: Filter?, ordering: Ordering) {
			self.filter = filter
			self.ordering = ordering
		}
	}
}

extension Series.FetchRequest {
	public enum Filter: Equatable {
		case id(Series.ID)
		case league(League)
	}
}

extension Series.FetchRequest {
	public enum Ordering {
		case byDate
	}
}
