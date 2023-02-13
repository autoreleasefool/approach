public protocol AnalyticsEvent {
	var name: String { get }
	var payload: [String: String]? { get }
}
