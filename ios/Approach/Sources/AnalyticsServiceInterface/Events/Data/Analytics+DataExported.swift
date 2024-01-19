extension Analytics.Data {
	public struct Exported: TrackableEvent {
		public let name = "Data.Exported"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
