extension Analytics.Backups {
	public struct Created: TrackableEvent {
		public let name = "Backup.Created"
		public let fileSizeBytes: Int

		public var payload: [String : String]? {
			[
				"FileSizeBytes": String(fileSizeBytes)
			]
		}

		public init(fileSizeBytes: Int) {
			self.fileSizeBytes = fileSizeBytes
		}
	}
}
