import SharedModelsLibrary

extension League {
	public struct Query {
		public let bowler: Bowler.ID
		public let ordering: Ordering

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
