public enum Analytics {}

public protocol TrackableEvent {
	var name: String { get }
	var payload: [String: String]? { get }
}

extension Analytics {
	public enum App {}
	public enum Bowler {}
	public enum Feature {}
	public enum Game {}
	public enum League {}
	public enum Series {}
}
