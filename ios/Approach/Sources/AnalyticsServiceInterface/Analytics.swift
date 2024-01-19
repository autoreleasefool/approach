import Foundation

public enum Analytics {}

extension Analytics {
	public enum OptInStatus: String {
		case optedIn
		case optedOut
	}
}

public protocol TrackableEvent {
	var name: String { get }
	var payload: [String: String]? { get }
}

public protocol GameSessionTrackableEvent: TrackableEvent {
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
	public enum Statistic {}
	public enum Widget {}
}
