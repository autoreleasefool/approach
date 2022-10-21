import SharedModelsLibrary

extension Series {
	public struct FetchRequest {
		public var league: League.ID
		public var ordering: Ordering

		public init(league: League.ID, ordering: Ordering) {
			self.league = league
			self.ordering = ordering
		}
	}
}

extension Series.FetchRequest {
	public enum Ordering {
		case byDate
	}
}
