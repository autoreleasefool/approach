extension Analytics.Series {
	public struct Created: TrackableEvent {
		public let name = "Series.League"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
