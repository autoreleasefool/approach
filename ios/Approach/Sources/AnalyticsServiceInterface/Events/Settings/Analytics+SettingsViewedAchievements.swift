extension Analytics.Settings {
	public struct ViewedAchievements: TrackableEvent {
		public let name = "Settings.ViewedAchievements"
		public let unseenAchievements: Int

		public var payload: [String: String]? {
			[
				"Unseen": "\(unseenAchievements)"
			]
		}

		public init(unseenAchievements: Int) {
			self.unseenAchievements = unseenAchievements
		}
	}
}
