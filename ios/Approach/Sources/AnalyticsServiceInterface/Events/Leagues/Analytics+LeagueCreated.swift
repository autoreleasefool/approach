extension Analytics.League {
	public struct Created: TrackableEvent {
		public let name = "League.Created"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
