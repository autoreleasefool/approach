extension Analytics.App {
	public struct Launched: TrackableEvent {
		public let name = "App.Launched"
		public let payload: [String: String]? = nil

		public init() {}
	}
}

