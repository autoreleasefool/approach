import StringsLibrary
import SwiftUI

struct AppIconSection: View {
	let appIconImage: UIImage
	let onTapAppIconButton: () -> Void

	var body: some View {
		Section {
			Button(action: onTapAppIconButton) {
				AppIconView(
					Strings.Settings.AppIcon.title,
					icon: .image(appIconImage),
					isCompact: true
				)
			}
			.buttonStyle(.navigation)
		}
	}
}

struct AppIconListSection: View {
	let appIcon: UIImage

	var body: some View {
		Section(Strings.Settings.AppIcon.title) {
			NavigationLink(value: SettingsList.SettingsItem.appIcon) {
				AppIconView(
					Strings.Settings.AppIcon.title,
					message: Strings.Settings.AppIcon.message,
					icon: .image(appIcon),
					isCompact: true
				)
			}
		}
	}
}
