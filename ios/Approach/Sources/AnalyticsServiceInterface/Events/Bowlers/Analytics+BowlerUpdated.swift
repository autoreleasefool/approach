extension Analytics.Bowler {
	public struct Updated: TrackableEvent {
		public let name = "Bowler.Updated"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
