extension Analytics.Series {
	public struct Shared: TrackableEvent {
		public let name = "Series.Shared"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
