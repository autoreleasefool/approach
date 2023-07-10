public enum Analytics {}

public protocol TrackableEvent {
	var name: String { get }
	var payload: [String: String]? { get }
}

extension Analytics {
	public enum App {}
	public enum Bowler {}
	public enum League {}
	public enum Feature {}
}
