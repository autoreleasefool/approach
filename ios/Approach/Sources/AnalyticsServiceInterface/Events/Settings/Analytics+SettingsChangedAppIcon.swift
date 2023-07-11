extension Analytics.Settings {
	public struct ChangedAppIcon: TrackableEvent {
		public let name = "Settings.ChangedAppIcon"

		public let appIconName: String

		public var payload: [String: String]? {
			[
				"AppIcon": appIconName,
			]
		}

		public init(appIconName: String) {
			self.appIconName = appIconName
		}
	}
}
