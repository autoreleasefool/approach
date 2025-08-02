import StringsLibrary
import SwiftUI

struct DeveloperOptionsSection: View {
	var body: some View {
		Section(Strings.Settings.DeveloperOptions.title) {
			SettingsLink(
				title: Strings.Settings.FeatureFlags.title,
				destination: SettingsList.SettingsItem.featureFlags
			)

			SettingsLink(
				title: Strings.Settings.DeveloperOptions.title,
				destination: SettingsList.SettingsItem.developerOptions
			)
		}
	}
}
