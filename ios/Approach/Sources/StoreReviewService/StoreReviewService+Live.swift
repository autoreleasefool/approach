import AppInfoServiceInterface
import Dependencies
import Foundation
import PreferenceServiceInterface
import StoreReviewServiceInterface

extension StoreReviewService: DependencyKey {
	public static var liveValue: Self = {
		return Self(
			shouldRequestReview: {
				@Dependency(AppInfoService.self) var appInfo
				let numberOfSessions = appInfo.numberOfSessions()

				@Dependency(PreferenceService.self) var preferences
				let lastReviewRequest = Date(timeIntervalSince1970: preferences.double(forKey: .appLastReviewRequestDate) ?? 0)

				@Dependency(\.date) var date
				@Dependency(\.calendar) var calendar
				let daysSinceLastRequest = calendar.dateComponents([.day], from: lastReviewRequest, to: date.now).day ?? 0

				let installDate = appInfo.installDate()
				let daysSinceInstall = calendar.dateComponents([.day], from: installDate, to: date.now).day ?? 0

				let lastReviewedVersion = preferences.string(forKey: .appLastReviewVersion) ?? ""

				return numberOfSessions >= 3 &&
					daysSinceLastRequest >= 7 &&
					daysSinceInstall >= 7 &&
					lastReviewedVersion != appInfo.appVersion()
			},
			didRequestReview: {
				@Dependency(AppInfoService.self) var appInfo
				@Dependency(PreferenceService.self) var preferences
				@Dependency(\.date) var date
				preferences.setKey(.appLastReviewRequestDate, toDouble: date.now.timeIntervalSince1970)
				preferences.setKey(.appLastReviewVersion, toString: appInfo.appVersion())
			}
		)
	}()
}
