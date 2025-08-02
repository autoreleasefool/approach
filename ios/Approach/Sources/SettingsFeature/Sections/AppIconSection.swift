import StringsLibrary
import SwiftUI

struct AppIconSection: View {
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
