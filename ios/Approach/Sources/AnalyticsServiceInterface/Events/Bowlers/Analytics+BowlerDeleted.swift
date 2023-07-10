extension Analytics.Bowler {
	public struct Deleted: TrackableEvent {
		public let name = "Bowler.Deleted"

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
