extension Analytics.Bowler {
	public struct Updated: TrackableEvent {
		public let name = "Bowler.Updated"

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
