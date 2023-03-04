import SharedModelsLibrary

extension Frame {
	public struct FetchRequest: Equatable {
		public let filter: Filter?
		public let ordering: Ordering

		public init(filter: Filter?, ordering: Ordering) {
			self.filter = filter
			self.ordering = ordering
		}
	}
}

extension Frame.FetchRequest {
	public enum Filter: Equatable {
		case id(Frame.ID)
		case game(Game)
	}
}

extension Frame.FetchRequest {
	public enum Ordering {
		case byOrdinal
	}
}
