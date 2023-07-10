extension Analytics.Alley {
	public struct Deleted: TrackableEvent {
		public let name = "Alley.Deleted"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
