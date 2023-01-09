import SharedModelsLibrary

extension League {
	public struct FetchRequest {
		public let filter: [Filter]
		public let ordering: Ordering

		public init(filter: [Filter], ordering: Ordering) {
			self.filter = filter
			self.ordering = ordering
		}
	}
}

extension League.FetchRequest {
	public enum Filter: Equatable {
		case id(League.ID)
		case bowler(Bowler.ID) // TODO: replace with bowler
		case recurrence(League.Recurrence)
	}
}

extension League.FetchRequest {
	public enum Ordering {
		case byName
		case byRecentlyUsed
	}
}
