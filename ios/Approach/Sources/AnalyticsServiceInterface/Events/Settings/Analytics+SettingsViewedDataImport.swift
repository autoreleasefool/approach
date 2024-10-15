extension Analytics.Settings {
	public struct ViewedDataImport: TrackableEvent {
		public let name = "Settings.ViewedDataImport"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
