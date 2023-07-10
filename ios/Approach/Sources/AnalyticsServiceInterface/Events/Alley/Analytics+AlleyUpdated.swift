extension Analytics.Alley {
	public struct Updated: TrackableEvent {
		public let name = "Alley.Updated"

		public let withLocation: Bool
		public let numberOfLanes: Int
		public var payload: [String: String]? {
			[
				"WithLocation": String(withLocation),
				"NumberOfLanes": String(numberOfLanes),
			]
		}

		public init(withLocation: Bool, numberOfLanes: Int) {
			self.withLocation = withLocation
			self.numberOfLanes = numberOfLanes
		}
	}
}
