extension Analytics.Alley {
	public struct Viewed: TrackableEvent {
		public let name = "Alley.Viewed"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
