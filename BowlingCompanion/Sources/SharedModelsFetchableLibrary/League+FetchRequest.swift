import SharedModelsLibrary

extension League {
	public struct FetchRequest {
		public let bowler: Bowler.ID
		public let ordering: Ordering

		public init(bowler: Bowler.ID, ordering: Ordering) {
			self.bowler = bowler
			self.ordering = ordering
		}
	}
}

extension League.FetchRequest {
	public enum Ordering {
		case byName
		case byRecentlyUsed
	}
}
