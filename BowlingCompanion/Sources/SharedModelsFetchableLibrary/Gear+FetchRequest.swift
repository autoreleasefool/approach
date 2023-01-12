import SharedModelsLibrary

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
		case bowler(Bowler.ID) // TODO: replace with bowler
		case kind(Gear.Kind)
	}
}

extension Gear.FetchRequest {
	public enum Ordering {
		case byRecentlyUsed
		case byName
	}
}
