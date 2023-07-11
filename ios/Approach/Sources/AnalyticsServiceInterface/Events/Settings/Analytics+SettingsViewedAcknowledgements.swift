extension Analytics.Settings {
	public struct ViewedAcknowledgements: TrackableEvent {
		public let name = "Settings.ViewedAcknowledgements"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
