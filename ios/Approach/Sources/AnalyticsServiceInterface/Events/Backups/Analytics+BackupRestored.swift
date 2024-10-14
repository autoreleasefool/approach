extension Analytics.Backups {
	public struct Restored: TrackableEvent {
		public let name = "Backup.Restored"
		public let payload: [String : String]? = nil

		public init() {}
	}
}
