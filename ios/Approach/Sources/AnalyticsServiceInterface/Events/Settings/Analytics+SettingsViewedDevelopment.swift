extension Analytics.Settings {
	public struct ViewedDevelopment: TrackableEvent {
		public let name = "Settings.ViewedDevelopment"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
