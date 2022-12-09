import SharedModelsLibrary

extension Gear {
	public struct FetchRequest {
		public let bowler: Bowler.ID?
		public let kind: Kind?
		public let ordering: Ordering

		public init(bowler: Bowler.ID? = nil, kind: Kind? = nil, ordering: Ordering) {
			self.bowler = bowler
			self.kind = kind
			self.ordering = ordering
		}
	}
}

extension Gear.FetchRequest {
	public enum Ordering {
		case byRecentlyUsed
		case byName
	}
}
