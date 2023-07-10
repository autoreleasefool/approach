extension Analytics.Series {
	public struct Viewed: TrackableEvent {
		public let name = "Series.Viewed"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
