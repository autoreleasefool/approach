extension Analytics.Game {
	public struct Updated: TrackableEvent {
		public let name = "Game.Updated"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
