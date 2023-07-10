import Foundation

extension Analytics.MatchPlay {
	public struct Created: GameSessionTrackableEvent {
		public let eventId: UUID
		public let name = "MatchPlay.Created"
		public var payload: [String: String]? { nil }

		public init(matchPlayId: UUID) {
			self.eventId = matchPlayId
		}
	}
}
