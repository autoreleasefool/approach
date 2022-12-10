import SharedModelsLibrary

extension Game {
	public struct FetchRequest {
		public let filter: [Filter]
		public let ordering: Ordering

		public init(filter: [Filter], ordering: Ordering) {
			self.filter = filter
			self.ordering = ordering
		}
	}
}

extension Game.FetchRequest {
	public enum Filter {
		case id(Game.ID)
		case series(Series.ID)
	}
}

extension Game.FetchRequest {
	public enum Ordering {
		case byOrdinal
	}
}
