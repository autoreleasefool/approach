extension Analytics.Bowler {
	public struct Viewed: TrackableEvent {
		public let name = "Bowler.Viewed"

		public let kind: String

		public var payload: [String: String]? {
			[
				"Kind": kind,
			]
		}

		public init(kind: String) {
			self.kind = kind
		}
	}
}
