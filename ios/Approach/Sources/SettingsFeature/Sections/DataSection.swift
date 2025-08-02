import AssetsLibrary
import AutomaticBackupsFeature
import DateTimeLibrary
import StringsLibrary
import SwiftUI

struct DataSection: View {
	let isBackupsButtonVisible: Bool
	let daysSinceLastBackup: DaysSince
	let daysSinceLastExport: DaysSince

	var body: some View {
		Section(Strings.Settings.Data.title) {
			SettingsLink(
				title: Strings.Settings.Archive.title,
				subtitle: Strings.Settings.Archive.footer,
				destination: SettingsList.SettingsItem.archive
			)

			SettingsLink(
				title: Strings.Settings.Data.import,
				destination: SettingsList.SettingsItem.import
			)

			NavigationLink(value: SettingsList.SettingsItem.export) {
				HStack {
					let (warningImage, warningImageColor) = daysSinceLastExport.warningImage()
					Image(systemName: warningImage)
						.foregroundStyle(warningImageColor)

					Text(Strings.Settings.Data.export)
				}
			}

			if isBackupsButtonVisible {
				NavigationLink(value: SettingsList.SettingsItem.backups) {
					HStack {
						let (warningImage, warningImageColor) = daysSinceLastBackup.warningImage()
						Image(systemName: warningImage)
							.foregroundStyle(warningImageColor)

						Text(Strings.Settings.Data.automaticBackups)
					}
				}
			}
		}
	}
}
