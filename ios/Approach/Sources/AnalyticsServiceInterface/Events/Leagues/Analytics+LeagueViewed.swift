extension Analytics.League {
	public struct Viewed: TrackableEvent {
		public let name = "League.Viewed"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
