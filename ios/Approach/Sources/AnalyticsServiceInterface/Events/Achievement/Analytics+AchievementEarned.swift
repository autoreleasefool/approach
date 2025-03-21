extension Analytics.Achievement {
	public struct Earned: TrackableEvent {
		public let name = "Achievement.Earned"

		public let achievement: String

		public var payload: [String: String]? {
			[
				"Achievement": achievement,
			]
		}

		public init(achievement: String) {
			self.achievement = achievement
		}
	}
}
