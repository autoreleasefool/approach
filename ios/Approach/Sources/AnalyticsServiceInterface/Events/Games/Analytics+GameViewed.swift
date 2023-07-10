extension Analytics.Game {
	public struct Viewed: TrackableEvent {
		public let name = "Game.Viewed"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
