import SharedModelsLibrary

extension Frame {
	public struct Query {
		public var game: Game.ID

		public init(game: Game.ID) {
			self.game = game
		}
	}
}
