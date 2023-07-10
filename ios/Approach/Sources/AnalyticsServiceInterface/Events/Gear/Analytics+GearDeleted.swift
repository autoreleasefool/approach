extension Analytics.Gear {
	public struct Deleted: TrackableEvent {
		public let name = "Gear.Deleted"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
