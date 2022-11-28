import SharedModelsLibrary

extension Alley {
	public struct FetchRequest {
		public let filter: Filter?
		public let ordering: Ordering

		public init(filter: Filter? = nil, ordering: Ordering) {
			self.filter = filter
			self.ordering = ordering
		}
	}
}

extension Alley.FetchRequest {
	public enum Filter {
		case id(Alley.ID)
	}
}

extension Alley.FetchRequest {
	public enum Ordering {
		case byName
		case byRecentlyUsed
	}
}
