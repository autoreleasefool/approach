import Dependencies
import PreferenceServiceInterface
import TipsServiceInterface

extension TipsService: DependencyKey {
	public static var liveValue: Self = {
		return Self(
			shouldShowTip: { tip in
				@Dependency(\.preferences) var preferences
				return preferences.getBool(tip.preferenceKey) != true
			},
			hideTip: { tip in
				@Dependency(\.preferences) var preferences
				preferences.setBool(tip.preferenceKey, true)
			}
		)
	}()
}
