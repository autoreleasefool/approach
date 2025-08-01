import StringsLibrary
import SwiftUI

struct DeveloperOptionsSection: View {
	let onTapFeatureFlagsButton: () -> Void
	let onTapPopulateDatabaseButton: () -> Void

	var body: some View {
		Section {
			Button(action: onTapFeatureFlagsButton) {
				Text(Strings.Settings.FeatureFlags.title)
			}
			.buttonStyle(.navigation)

			Button(action: onTapPopulateDatabaseButton) {
				Text(Strings.Settings.DeveloperOptions.populateDatabase)
			}
		}
	}
}

struct DeveloperOptionsListSection: View {
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
