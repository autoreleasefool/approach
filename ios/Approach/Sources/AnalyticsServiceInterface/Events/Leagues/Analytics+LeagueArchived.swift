extension Analytics.League {
	public struct Archived: TrackableEvent {
		public let name = "League.Archived"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
