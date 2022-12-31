import SharedModelsLibrary

extension Bowler {
	public struct FetchRequest: Equatable {
		public let filter: [Filter]
		public let ordering: Ordering

		public init(filter: [Filter], ordering: Ordering) {
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
	public enum Ordering: Hashable, CaseIterable, CustomStringConvertible {
		case byName
		case byRecentlyUsed

		public var description: String {
			switch self {
			case .byRecentlyUsed: return "Most Recently Used"
			case .byName: return "Alphabetical"
			}
		}
	}
}
