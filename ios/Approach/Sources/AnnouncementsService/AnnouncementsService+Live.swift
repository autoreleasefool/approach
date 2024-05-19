import AnnouncementsLibrary
import AnnouncementsServiceInterface
import Dependencies
import UserDefaultsPackageServiceInterface

extension AnnouncementsService: DependencyKey {
	public static var liveValue: Self {
		Self(
			announcement: {
				@Dependency(\.userDefaults) var userDefaults
				return Announcement.allCases
					.first(where: {
						$0.meetsExpectationsToShow() && userDefaults.bool(forKey: $0.preferenceKey) != true
					})
			},
			hideAnnouncement: { announcement in
				@Dependency(\.userDefaults) var userDefaults
				userDefaults.setBool(forKey: announcement.preferenceKey, to: true)
			}
		)
	}
}
