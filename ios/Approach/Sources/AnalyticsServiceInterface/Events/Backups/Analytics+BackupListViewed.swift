extension Analytics.Backups {
	public struct ListViewed: TrackableEvent {
		public let name = "Backup.ListViewed"
		public let payload: [String: String]? = nil

		public init() {}
	}
}
