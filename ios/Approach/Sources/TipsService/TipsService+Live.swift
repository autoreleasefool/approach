import Dependencies
import PreferenceServiceInterface
import TipsServiceInterface

extension TipsService: DependencyKey {
	public static var liveValue: Self = {
		return Self(
			shouldShowTip: { tip in
				@Dependency(PreferenceService.self) var preferences
				return preferences.getBool(tip.preferenceKey) != true
			},
			hideTip: { tip in
				@Dependency(PreferenceService.self) var preferences
				preferences.setBool(tip.preferenceKey, true)
			}
		)
	}()
}
