extension Analytics.Gear {
	public struct Created: TrackableEvent {
		public let name = "Gear.Created"

		public let kind: String

		public var payload: [String: String]? {
			[
				"Kind": kind
			]
		}

		public init(kind: String) {
			self.kind = kind
		}
	}
}
