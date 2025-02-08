extension Analytics.Data {
	public struct Imported: TrackableEvent {
		public let name = "Data.Imported"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
