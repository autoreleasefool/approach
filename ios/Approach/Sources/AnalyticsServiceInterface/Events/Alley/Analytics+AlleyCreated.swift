extension Analytics.Alley {
	public struct Created: TrackableEvent {
		public let name = "Alley.Created"

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
