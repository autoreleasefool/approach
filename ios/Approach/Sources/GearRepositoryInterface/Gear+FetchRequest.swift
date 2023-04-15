import ModelsLibrary

extension Gear {
	public struct FetchRequest: Equatable {
		public let filter: Filter
		public let ordering: Ordering

		public init(filter: Filter, ordering: Ordering) {
			self.filter = filter
			self.ordering = ordering
		}
	}
}

extension Gear.FetchRequest {
	public struct Filter: Equatable {
		public let bowler: Bowler.ID?
		public let kind: Gear.Kind?

		public init(
			bowler: Bowler.ID?,
			kind: Gear.Kind?
		) {
			self.bowler = bowler
			self.kind = kind
		}
	}
}

extension Gear.FetchRequest {
	public enum Ordering: Hashable, CaseIterable {
		case byName
		case byRecentlyUsed
	}
}
