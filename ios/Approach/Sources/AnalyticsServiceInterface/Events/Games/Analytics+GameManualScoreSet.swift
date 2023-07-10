import Foundation

extension Analytics.Game {
	public struct ManualScoreSet: GameSessionTrackableEvent {
		public let eventId: UUID
		public let name = "Game.ManualScoreSet"
		public var payload: [String: String]? { nil }

		public init(gameId: UUID) {
			self.eventId = gameId
		}
	}
}
