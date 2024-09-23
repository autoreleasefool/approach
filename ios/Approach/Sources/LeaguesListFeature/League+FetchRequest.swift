import LeaguesRepositoryInterface
import ModelsLibrary

extension League.List {
	public struct FetchRequest: Equatable, Sendable {
		public let filter: Filter
		public let ordering: League.Ordering

		public init(filter: Filter, ordering: League.Ordering) {
			self.filter = filter
			self.ordering = ordering
		}
	}
}

extension League.List.FetchRequest {
	public struct Filter: Equatable, Sendable {
		public let bowler: Bowler.ID
		public var recurrence: League.Recurrence?

		public init(
			bowler: Bowler.ID,
			recurrence: League.Recurrence? = nil
		) {
			self.bowler = bowler
			self.recurrence = recurrence
		}
	}
}
