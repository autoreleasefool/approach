extension Analytics.Settings {
	public struct ViewedSource: TrackableEvent {
		public let name = "Settings.ViewedSource"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
