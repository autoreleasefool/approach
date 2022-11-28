import SharedModelsLibrary

extension Alley {
	public struct Query {
		public let filter: Filter?
		public let ordering: Ordering

		public init(filter: Filter? = nil, ordering: Ordering) {
			self.filter = filter
			self.ordering = ordering
		}
	}
}

extension Alley.Query {
	public enum Filter {
		case id(Alley.ID)
	}
}

extension Alley.Query {
	public enum Ordering {
		case byName
	}
}
