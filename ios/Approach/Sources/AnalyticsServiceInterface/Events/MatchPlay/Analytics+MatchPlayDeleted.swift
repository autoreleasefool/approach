import Foundation

extension Analytics.MatchPlay {
	public struct Deleted: GameSessionTrackableEvent {
		public let eventId: UUID
		public let name = "MatchPlay.Deleted"
		public var payload: [String: String]? { nil }

		public init(matchPlayId: UUID) {
			self.eventId = matchPlayId
		}
	}
}
