extension Analytics.Bowler {
	public struct Created: TrackableEvent {
		public let name = "Bowler.Created"
		public let id: String

		public var payload: [String: String]? {
			["id": id]
		}

		public init(id: String) {
			self.id = id
		}
	}
}
