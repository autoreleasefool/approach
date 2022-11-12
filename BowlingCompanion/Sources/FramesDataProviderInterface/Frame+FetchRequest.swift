import SharedModelsLibrary

extension Frame {
	public struct FetchRequest {
		public var game: Game.ID

		public init(game: Game.ID) {
			self.game = game
		}
	}
}
