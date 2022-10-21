import SharedModelsLibrary

extension Frame {
	public struct FetchRequest {
		public var game: Game.ID
		public var ordering: Ordering

		public init(game: Game.ID, ordering: Ordering) {
			self.game = game
			self.ordering = ordering
		}
	}
}

extension Frame.FetchRequest {
	public enum Ordering {
		case byOrdinal
	}
}
