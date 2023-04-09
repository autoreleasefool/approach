import ModelsLibrary

extension League {
	public struct FetchRequest: Equatable {
		public let filter: Filter
		public let ordering: Ordering

		public init(filter: Filter, ordering: Ordering) {
			self.filter = filter
			self.ordering = ordering
		}
	}
}

extension League.FetchRequest {
	public struct Filter: Equatable {
		public let bowler: Bowler.ID
		public let recurrence: League.Recurrence?

		public init(
			bowler: Bowler.ID,
			recurrence: League.Recurrence?
		) {
			self.bowler = bowler
			self.recurrence = recurrence
		}
	}
}

extension League.FetchRequest {
	public enum Ordering: Hashable, CaseIterable {
		case byName
		case byRecentlyUsed
	}
}
