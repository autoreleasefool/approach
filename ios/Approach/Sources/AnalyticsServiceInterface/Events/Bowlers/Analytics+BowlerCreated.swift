extension Analytics.Bowler {
	public struct Created: TrackableEvent {
		public let name = "Bowler.Created"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
