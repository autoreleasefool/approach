import Foundation

extension Analytics.MatchPlay {
	public struct Updated: TrackableEvent {
		public let name = "MatchPlay.Updated"
		public let withOpponent: Bool
		public let withScore: Bool
		public let withResult: String

		public var payload: [String: String]? {
			[
				"WithOpponent": String(withOpponent),
				"WithScore": String(withScore),
				"WithResult": String(withResult),
			]
		}

		public init(
			withOpponent: Bool,
			withScore: Bool,
			withResult: String
		) {
			self.withOpponent = withOpponent
			self.withScore = withScore
			self.withResult = withResult
		}
	}
}
