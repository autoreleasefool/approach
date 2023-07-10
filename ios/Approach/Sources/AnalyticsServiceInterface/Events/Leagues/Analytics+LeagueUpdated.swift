extension Analytics.League {
	public struct Updated: TrackableEvent {
		public let name = "Bowler.League"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
