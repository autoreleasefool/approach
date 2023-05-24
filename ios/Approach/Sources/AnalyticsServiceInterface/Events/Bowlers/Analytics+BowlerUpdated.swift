extension Analytics.Bowler {
	public struct Updated: TrackableEvent {
		public let name = "Bowler.Updated"
		public let id: String

		public var payload: [String: String]? {
			["id": id]
		}

		public init(id: String) {
			self.id = id
		}
	}
}
