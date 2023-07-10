import Foundation

extension Analytics.MatchPlay {
	public struct Updated: GameSessionTrackableEvent {
		public let eventId: UUID
		public let name = "MatchPlay.Updated"
		public var payload: [String: String]? { nil }

		public init(matchPlayId: UUID) {
			self.eventId = matchPlayId
		}
	}
}
