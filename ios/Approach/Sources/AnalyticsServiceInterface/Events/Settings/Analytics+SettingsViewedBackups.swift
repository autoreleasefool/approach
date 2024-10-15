extension Analytics.Settings {
	public struct ViewedBackups: TrackableEvent {
		public let name = "Settings.ViewedBackups"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
