public struct TabSwitchEvent: AnalyticsEvent {
	public let name: String = ""
	public let tab: String

	public var payload: [String: String]? {
		["tab": tab]
	}

	public init(tab: String) {
		self.tab = tab
	}
}
