extension Analytics.Series {
	public struct Archived: TrackableEvent {
		public let name = "Series.Archived"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
