import AnnouncementsLibrary
import Dependencies

public struct AnnouncementsService: Sendable {
	public var announcement: @Sendable () -> Announcement?
	public var hideAnnouncement: @Sendable (Announcement) async -> Void

	public init(
		announcement: @escaping @Sendable () -> Announcement?,
		hideAnnouncement: @escaping @Sendable (Announcement) async -> Void
	) {
		self.announcement = announcement
		self.hideAnnouncement = hideAnnouncement
	}
}

extension AnnouncementsService: TestDependencyKey {
	public static var testValue: Self {
		Self(
			announcement: { unimplemented("\(Self.self).announcement", placeholder: .christmas2023) },
			hideAnnouncement: { _ in unimplemented("\(Self.self).hideAnnouncement")}
		)
	}
}
