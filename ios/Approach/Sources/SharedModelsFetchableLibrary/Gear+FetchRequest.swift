import SharedModelsLibrary
import StringsLibrary

extension Gear {
	public struct FetchRequest: Equatable {
		public let filter: Filter?
		public let ordering: Ordering

		public init(filter: Filter?, ordering: Ordering) {
			self.filter = filter
			self.ordering = ordering
		}
	}
}

extension Gear.FetchRequest {
	public enum Filter: Equatable {
		case id(Gear.ID)
		case bowler(Bowler)
		case kind(Gear.Kind)
	}
}

extension Gear.FetchRequest {
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
