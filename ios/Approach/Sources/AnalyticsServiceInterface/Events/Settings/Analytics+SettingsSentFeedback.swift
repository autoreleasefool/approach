extension Analytics.Settings {
	public struct SentFeedback: TrackableEvent {
		public let name = "Settings.SentFeedback"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
