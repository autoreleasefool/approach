import SharedModelsLibrary

extension League {
	public struct Query {
		public var bowler: Bowler.ID
		public var ordering: Ordering

		public init(bowler: Bowler.ID, ordering: Ordering) {
			self.bowler = bowler
			self.ordering = ordering
		}
	}
}

extension League.Query {
	public enum Ordering {
		case byName
	}
}
