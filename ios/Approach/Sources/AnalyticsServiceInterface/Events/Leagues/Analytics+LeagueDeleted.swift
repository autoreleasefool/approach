extension Analytics.League {
	public struct Deleted: TrackableEvent {
		public let name = "Bowler.League"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
