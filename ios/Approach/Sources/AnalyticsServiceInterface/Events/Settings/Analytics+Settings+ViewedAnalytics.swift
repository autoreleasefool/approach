extension Analytics.Settings {
	public struct ViewedAnalytics: TrackableEvent {
		public let name = "Settings.ViewedAnalytics"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
