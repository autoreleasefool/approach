import SharedModelsLibrary

extension Gear {
	public struct Query {
		public let bowler: Bowler.ID?
		public let kind: Kind?
		public let ordering: Ordering

		public init(bowler: Bowler.ID?, kind: Kind?, ordering: Ordering) {
			self.bowler = bowler
			self.kind = kind
			self.ordering = ordering
		}
	}
}

extension Gear.Query {
	public enum Ordering {
		case byName
	}
}
