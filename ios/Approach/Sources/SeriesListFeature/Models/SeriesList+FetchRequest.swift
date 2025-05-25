import ModelsLibrary
import SeriesRepositoryInterface
import Sharing

extension Series.List {
	public struct FetchRequest: Equatable, Sendable {
		public let league: League.ID
		public var ordering: Series.Ordering

		public init(league: League.ID, ordering: Series.Ordering) {
			self.league = league
			self.ordering = ordering
		}
	}
}
