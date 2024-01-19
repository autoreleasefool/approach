extension Analytics.Settings {
	public struct ViewedDataExport: TrackableEvent {
		public let name = "Settings.ViewedDataExport"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
