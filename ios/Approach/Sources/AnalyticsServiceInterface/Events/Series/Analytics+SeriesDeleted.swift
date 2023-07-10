extension Analytics.Series {
	public struct Deleted: TrackableEvent {
		public let name = "Series.League"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
