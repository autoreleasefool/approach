extension Analytics.Settings {
	public struct ReportedBug: TrackableEvent {
		public let name = "Settings.ReportedBug"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
