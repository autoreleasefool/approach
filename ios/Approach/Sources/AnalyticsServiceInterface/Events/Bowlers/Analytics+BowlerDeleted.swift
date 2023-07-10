extension Analytics.Bowler {
	public struct Deleted: TrackableEvent {
		public let name = "Bowler.Deleted"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
