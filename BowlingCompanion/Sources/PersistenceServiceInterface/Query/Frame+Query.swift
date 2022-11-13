import SharedModelsLibrary

extension Frame {
	public struct Query {
		public let game: Game.ID

		public init(game: Game.ID) {
			self.game = game
		}
	}
}
