extension Analytics.Bowler {
	public struct Viewed: TrackableEvent {
		public let name = "Bowler.Viewed"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
