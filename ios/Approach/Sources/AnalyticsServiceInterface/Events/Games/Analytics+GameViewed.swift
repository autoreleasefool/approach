import Foundation

extension Analytics.Game {
	public struct Viewed: GameSessionTrackableEvent {
		public let eventId: UUID
		public let name = "Game.Viewed"
		public var payload: [String: String]? { nil }

		public init(gameId: UUID) {
			self.eventId = gameId
		}
	}
}
