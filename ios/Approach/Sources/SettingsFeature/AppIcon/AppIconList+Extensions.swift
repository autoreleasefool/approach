import AppIconServiceInterface
import AssetsLibrary
import ComposableArchitecture
import UIKit

extension AppIconList {
	func fetchCurrentAppIcon() -> Effect<Action> {
		.run { send in
			await send(.internal(.didFetchIcon(Result { AppIcon(rawValue: await appIcon.getAppIconName() ?? "") })))
		}
	}
}

extension AppIconList.State {
	var appIconImage: UIImage {
		if let currentAppIcon {
			return currentAppIcon.image ?? UIImage()
		} else if isLoadingAppIcon {
			return UIImage()
		} else {
			return AppIcon.primary.image ?? UIImage()
		}
	}
}
