import AppInfoServiceInterface
import ConstantsLibrary
import Dependencies
import Foundation
import PreferenceServiceInterface

extension AppInfoService: DependencyKey {
	public static var liveValue: Self = {
		@Sendable func numberOfSessions() -> Int {
			@Dependency(\.preferences) var preferences
			return preferences.int(forKey: .appSessions) ?? 0
		}

		@Sendable func installDate() -> Date {
			@Dependency(\.preferences) var preferences
			return Date(timeIntervalSince1970: preferences.double(forKey: .appInstallDate) ?? 0)
		}

		return Self(
			recordNewSession: {
				@Dependency(\.preferences) var preferences
				preferences.setKey(.appSessions, toInt: numberOfSessions() + 1)
			},
			numberOfSessions: numberOfSessions,
			recordInstallDate: {
				guard installDate().timeIntervalSince1970 == 0 else { return }
				@Dependency(\.preferences) var preferences
				@Dependency(\.date) var date
				preferences.setKey(.appInstallDate, toDouble: date.now.timeIntervalSince1970)
			},
			installDate: installDate,
			appVersion: {
				AppConstants.appVersionReadable
			}
		)
	}()
}
