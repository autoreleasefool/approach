import SharedModelsLibrary

extension Bowler {
	public struct Query {
		public let ordering: Ordering

		public init(ordering: Ordering) {
			self.ordering = ordering
		}
	}
}

extension Bowler.Query {
	public enum Ordering {
		case byName
	}
}
