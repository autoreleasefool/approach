@_exported import AnalyticsPackageServiceInterface
import Foundation

public protocol GameSessionTrackableEvent: Sendable {
	var name: String { get }
	var payload: [String: String]? { get }
	var eventId: UUID { get }
}

extension Analytics {
	public enum Alley {}
	public enum Announcement {}
	public enum App {}
	public enum Bowler {}
	public enum Data {}
	public enum Feature {}
	public enum Game {}
	public enum Gear {}
	public enum League {}
	public enum MatchPlay {}
	public enum Series {}
	public enum Settings {}
	public enum Sharing {}
	public enum Statistic {}
	public enum Widget {}
}
