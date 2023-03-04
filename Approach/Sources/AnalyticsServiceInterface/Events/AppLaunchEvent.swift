public struct AppLaunchEvent: AnalyticsEvent {
	public let name: String = "AppLaunch"
	public let payload: [String: String]? = nil

	public init() {}
}
