import SharedModelsLibrary
import StringsLibrary

// MARK: - SingleFetchRequest

extension Bowler {
	public struct SingleFetchRequest: Equatable {
		public let filter: Filter

		public init(filter: Filter) {
			self.filter = filter
		}
	}
}

extension Bowler.SingleFetchRequest {
	public enum Filter: Equatable {
		case id(Bowler.ID)
		case owner(Game)
	}
}

// MARK: - FetchRequest

extension Bowler {
	public struct FetchRequest: Equatable {
		public let filter: Filter?
		public let ordering: Ordering

		public init(filter: Filter?, ordering: Ordering) {
			self.filter = filter
			self.ordering = ordering
		}
	}
}

extension Bowler.FetchRequest {
	public enum Filter: Equatable {
		case name(String)
	}
}

extension Bowler.FetchRequest {
	public enum Ordering: Hashable, CaseIterable, CustomStringConvertible {
		case byName
		case byRecentlyUsed

		public var description: String {
			switch self {
			case .byRecentlyUsed: return Strings.Ordering.mostRecentlyUsed
			case .byName: return Strings.Ordering.alphabetical
			}
		}
	}
}
