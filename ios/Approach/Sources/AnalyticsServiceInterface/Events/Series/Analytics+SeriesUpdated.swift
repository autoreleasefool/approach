extension Analytics.Series {
	public struct Updated: TrackableEvent {
		public let name = "Series.League"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
