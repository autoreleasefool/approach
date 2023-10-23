extension Analytics.Settings {
	public struct ViewedArchived: TrackableEvent {
		public let name = "Settings.ViewedArchived"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
