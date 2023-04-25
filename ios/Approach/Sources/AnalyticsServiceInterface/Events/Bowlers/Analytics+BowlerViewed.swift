extension Analytics.Bowler {
	public struct Viewed: TrackableEvent {
		public let name = "Bowler.Viewed"
		public let id: String

		public var payload: [String: String]? {
			["id": id]
		}

		public init(id: String) {
			self.id = id
		}
	}
}
