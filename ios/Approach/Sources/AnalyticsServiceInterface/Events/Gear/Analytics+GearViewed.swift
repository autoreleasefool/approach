extension Analytics.Gear {
	public struct Viewed: TrackableEvent {
		public let name = "Gear.Viewed"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
