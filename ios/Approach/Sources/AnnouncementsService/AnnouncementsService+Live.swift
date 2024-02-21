import AnnouncementsLibrary
import AnnouncementsServiceInterface
import Dependencies
import PreferenceServiceInterface

extension AnnouncementsService: DependencyKey {
	public static var liveValue: Self = {
		Self(
			announcement: {
				@Dependency(\.preferences) var preferences
				return Announcement.allCases
					.first(where: {
						$0.meetsExpectationsToShow() && preferences.getBool($0.preferenceKey) != true
					})
			},
			hideAnnouncement: { announcement in
				@Dependency(\.preferences) var preferences
				preferences.setBool(announcement.preferenceKey, true)
			}
		)
	}()
}
