extension Analytics.App {
	public struct TabSwitched: TrackableEvent {
		public let name = "App.TabSwitched"
		public let tab: String

		public var payload: [String: String]? {
			["tab": tab]
		}

		public init(tab: String) {
			self.tab = tab
		}
	}
}
