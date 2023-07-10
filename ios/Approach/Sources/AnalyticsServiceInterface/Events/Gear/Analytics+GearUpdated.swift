extension Analytics.Gear {
	public struct Updated: TrackableEvent {
		public let name = "Gear.Updated"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
