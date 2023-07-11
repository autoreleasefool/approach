extension Analytics.Series {
	public struct Updated: TrackableEvent {
		public let name = "Series.Updated"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
