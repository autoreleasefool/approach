import SharedModelsLibrary

extension Bowler {
	public struct Query {
		public let filter: [Filter]
		public let ordering: Ordering

		public init(filter: [Filter], ordering: Ordering) {
			self.filter = filter
			self.ordering = ordering
		}
	}
}

extension Bowler.Query {
	public enum Filter {
		case id(Bowler.ID)
		case name(String)
	}
}

extension Bowler.Query {
	public enum Ordering {
		case byName
	}
}
