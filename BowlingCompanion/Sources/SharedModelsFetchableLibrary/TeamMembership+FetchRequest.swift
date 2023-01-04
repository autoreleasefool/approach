import SharedModelsLibrary
import StringsLibrary

extension TeamMembership {
	public struct FetchRequest: Equatable {
		public let filter: Filter
		public let ordering: Ordering

		public init(filter: Filter, ordering: Ordering) {
			self.filter = filter
			self.ordering = ordering
		}
	}
}

extension TeamMembership.FetchRequest {
	public enum Filter: Equatable {
		case id(Team.ID)
	}
}

extension TeamMembership.FetchRequest {
	public enum Ordering: Hashable, CaseIterable, CustomStringConvertible {
		case byName

		public var description: String {
			switch self {
			case .byName: return Strings.Ordering.alphabetical
			}
		}
	}
}
