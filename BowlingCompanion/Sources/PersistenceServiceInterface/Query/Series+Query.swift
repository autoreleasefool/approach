import SharedModelsLibrary

extension Series {
	public struct Query {
		public let league: League.ID
		public let ordering: Ordering

		public init(league: League.ID, ordering: Ordering) {
			self.league = league
			self.ordering = ordering
		}
	}
}

extension Series.Query {
	public enum Ordering {
		case byDate
	}
}
