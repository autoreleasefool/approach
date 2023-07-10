extension Analytics.Game {
	public struct ManualScoreSet: TrackableEvent {
		public let name = "Game.ManualScoreSet"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
