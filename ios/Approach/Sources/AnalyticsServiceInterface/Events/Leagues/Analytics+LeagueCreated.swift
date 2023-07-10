extension Analytics.League {
	public struct Created: TrackableEvent {
		public let name = "Bowler.League"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
