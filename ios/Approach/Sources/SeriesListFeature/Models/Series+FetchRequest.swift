import ModelsLibrary
import SeriesRepositoryInterface

extension Series.List {
	public struct FetchRequest: Equatable, Sendable {
		public let league: League.ID
		public let ordering: Series.Ordering

		public init(league: League.ID, ordering: Series.Ordering) {
			self.league = league
			self.ordering = ordering
		}
	}
}
