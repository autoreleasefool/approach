import SharedModelsLibrary

extension Frame {
	public struct FetchRequest {
		public let filter: [Filter]
		public let ordering: Ordering

		public init(filter: [Filter], ordering: Ordering) {
			self.filter = filter
			self.ordering = ordering
		}
	}
}

extension Frame.FetchRequest {
	public enum Filter {
		case id(Frame.ID)
		case game(Game.ID)
	}
}

extension Frame.FetchRequest {
	public enum Ordering {
		case byOrdinal
	}
}
