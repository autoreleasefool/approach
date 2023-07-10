import Foundation

extension Analytics.Game {
	public struct Updated: GameSessionTrackableEvent {
		public let eventId: UUID
		public let name = "Game.Updated"
		public var payload: [String: String]? { nil }

		public init(gameId: UUID) {
			self.eventId = gameId
		}
	}
}
