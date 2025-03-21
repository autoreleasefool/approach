extension Analytics.Achievement {
	public struct ListViewed: TrackableEvent {
		public let name = "Achievement.ListViewed"

		public var payload: [String: String]? { nil }

		public init() {}
	}
}
