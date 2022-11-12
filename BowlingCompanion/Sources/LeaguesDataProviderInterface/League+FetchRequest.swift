import SharedModelsLibrary

extension League {
	public struct FetchRequest {
		public var bowler: Bowler.ID
		public var ordering: Ordering

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
