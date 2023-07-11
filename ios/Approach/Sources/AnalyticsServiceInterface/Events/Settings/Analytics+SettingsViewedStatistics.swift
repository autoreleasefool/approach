extension Analytics.Settings {
	public struct ViewedStatistics: TrackableEvent {
		public let name = "Settings.ViewedStatistics"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
