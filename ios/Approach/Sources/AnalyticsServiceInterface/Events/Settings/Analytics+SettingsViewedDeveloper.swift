extension Analytics.Settings {
	public struct ViewedDeveloper: TrackableEvent {
		public let name = "Settings.ViewedDeveloper"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
