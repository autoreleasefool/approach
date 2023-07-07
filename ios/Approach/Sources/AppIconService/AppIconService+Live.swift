import AppIconServiceInterface
import AssetsLibrary
import Dependencies
import UIKit

extension AppIconService: DependencyKey {
	public static var liveValue = Self(
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
