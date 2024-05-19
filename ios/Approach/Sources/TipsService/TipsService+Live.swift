import Dependencies
import TipsServiceInterface
import UserDefaultsPackageServiceInterface

extension TipsService: DependencyKey {
	public static var liveValue: Self {
		Self(
			shouldShowTip: { tip in
				@Dependency(\.userDefaults) var userDefaults
				return userDefaults.bool(forKey: tip.preferenceKey) != true
			},
			hideTip: { tip in
				@Dependency(\.userDefaults) var userDefaults
				userDefaults.setBool(forKey: tip.preferenceKey, to: true)
			}
		)
	}
}
