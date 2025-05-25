import LeaguesRepositoryInterface
import ModelsLibrary
import Sharing

extension League.List {
	public struct FetchRequest: Equatable, Sendable {
		public var filter: Filter
		public var ordering: League.Ordering

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

extension SharedReaderKey where Self == AppStorageKey<League.Ordering>.Default {
	static var ordering: Self {
		Self[
			.appStorage("leaguesList_ordering"),
			default: .byRecentlyUsed
		]
	}
}
