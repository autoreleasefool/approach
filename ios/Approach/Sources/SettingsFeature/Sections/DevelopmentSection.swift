import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import ViewsLibrary

struct DevelopmentSection: View {
	var body: some View {
		Section(Strings.Settings.Development.title) {
			SettingsLink(
				title: Strings.Settings.Development.title,
				destination: SettingsList.SettingsItem.development
			)
		}
	}
}
