extension Analytics.Bowler {
	public struct Deleted: TrackableEvent {
		public let name = "Bowler.Deleted"
		public let id: String

		public var payload: [String: String]? {
			["id": id]
		}

		public init(id: String) {
			self.id = id
		}
	}
}
