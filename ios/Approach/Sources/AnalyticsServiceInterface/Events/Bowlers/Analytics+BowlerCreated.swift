extension Analytics.Bowler {
	public struct Created: TrackableEvent {
		public let name = "Bowler.Created"

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
