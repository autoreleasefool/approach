import SharedModelsLibrary

extension Bowler {
	public struct FetchRequest: Equatable {
		public let filter: [Filter]
		public let ordering: Ordering

		public init(filter: [Filter] = [], ordering: Ordering) {
			self.filter = filter
			self.ordering = ordering
		}
	}
}

extension Bowler.FetchRequest {
	public enum Filter: Equatable {
		case id(Bowler.ID)
		case name(String)
	}
}

extension Bowler.FetchRequest {
	public enum Ordering: Equatable {
		case byName
		case byRecentlyUsed
	}
}
