extension Analytics.Announcement {
	public struct ChristmasAnnouncementShown: TrackableEvent {
		public let name = "Announcement.ChristmasAnnouncementShown"
		public let payload: [String: String]? = nil

		public init() {}
	}
}
