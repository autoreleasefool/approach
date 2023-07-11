extension Analytics.League {
	public struct Deleted: TrackableEvent {
		public let name = "League.Deleted"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
