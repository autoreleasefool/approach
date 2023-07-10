extension Analytics.Settings {
	public struct ViewedOpponents: TrackableEvent {
		public let name = "Settings.ViewedOpponents"
		public var payload: [String : String]? { nil }

		public init() {}
	}
}

