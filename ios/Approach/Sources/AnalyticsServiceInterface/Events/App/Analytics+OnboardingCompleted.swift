extension Analytics.App {
	public struct OnboardingCompleted: TrackableEvent {
		public let name = "App.OnboardingCompleted"
		public let payload: [String: String]? = nil

		public init() {}
	}
}
