import AchievementsLibrary
import AchievementsServiceInterface
import AppIconServiceInterface
import AssetsLibrary
import Dependencies
import FeatureFlagsLibrary
import UIKit

extension AppIconService: DependencyKey {
	public static var liveValue: Self {
		Self(
			availableAppIcons: {
				await withTaskGroup(of: AppIcon?.self) { group in
					for appIcon in AppIcon.allCases {
						group.addTask { await appIcon.isAvailable() ? appIcon : nil }
					}

					return Set<AppIcon>(
						await group.reduce(into: []) { result, appIcon in
							if let appIcon {
								result.append(appIcon)
							}
						}
					)
				}
			},
			setAppIcon: { @MainActor appIcon in
				try await UIApplication.shared.setAlternateIconName(appIcon.rawValue)
			},
			resetAppIcon: { @MainActor in
				try await UIApplication.shared.setAlternateIconName(nil)
			},
			getAppIconName: { @MainActor in
				UIApplication.shared.alternateIconName
			},
			supportsAlternateIcons: { @MainActor in
				UIApplication.shared.supportsAlternateIcons
			}
		)
	}
}

extension AppIcon {
	func isAvailable() async -> Bool {
		switch self {
		case .primary, .dark, .purple,
				.christmas,
				.candyCorn, .witchHat, .devilHorns,
				.pride, .bisexual, .trans:
			return true
		case .fabric:
			@Dependency(\.featureFlags) var featureFlags
			@Dependency(AchievementsService.self) var achievements

			guard featureFlags.isFlagEnabled(.achievements) else { return false }

			return await achievements.hasEarnedAchievement(EarnableAchievements.TenYears.self)
		}
	}
}
