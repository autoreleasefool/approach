import SharedModelsLibrary

extension League {
	public struct FetchRequest: Equatable {
		public let filter: Filter?
		public let ordering: Ordering

		public init(filter: Filter?, ordering: Ordering) {
			self.filter = filter
			self.ordering = ordering
		}
	}
}

extension League.FetchRequest {
	public enum Filter: Equatable {
		case id(League.ID)
		case properties(Bowler.ID, recurrence: League.Recurrence?) // TODO: replace with bowler
	}
}

extension League.FetchRequest {
	public enum Ordering {
		case byName
		case byRecentlyUsed
	}
}
