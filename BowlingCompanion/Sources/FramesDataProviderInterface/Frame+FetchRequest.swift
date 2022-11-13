import SharedModelsLibrary

extension Frame {
	public struct FetchRequest {
		public let game: Game.ID
		public let ordering: Ordering

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
