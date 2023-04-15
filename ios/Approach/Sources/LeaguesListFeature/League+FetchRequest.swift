import LeaguesRepositoryInterface
import ModelsLibrary

extension League.Summary {
	public struct FetchRequest: Equatable {
		public let filter: Filter
		public let ordering: League.Ordering

		public init(filter: Filter, ordering: League.Ordering) {
			self.filter = filter
			self.ordering = ordering
		}
	}
}

extension League.Summary.FetchRequest {
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
