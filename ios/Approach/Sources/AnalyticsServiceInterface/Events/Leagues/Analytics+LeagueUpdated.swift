extension Analytics.League {
	public struct Updated: TrackableEvent {
		public let name = "League.Updated"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
