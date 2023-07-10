import Foundation

public enum Analytics {}

public protocol TrackableEvent {
	var name: String { get }
	var payload: [String: String]? { get }
}

public protocol GameSessionTrackableEvent: TrackableEvent {
	var eventId: UUID { get }
}

extension Analytics {
	public enum Alley {}
	public enum App {}
	public enum Bowler {}
	public enum Feature {}
	public enum Game {}
	public enum Gear {}
	public enum League {}
	public enum MatchPlay {}
	public enum Series {}
}
