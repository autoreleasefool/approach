extension Analytics.Settings {
	public struct ViewedAppIcons: TrackableEvent {
		public let name = "Settings.ViewedAppIcons"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
