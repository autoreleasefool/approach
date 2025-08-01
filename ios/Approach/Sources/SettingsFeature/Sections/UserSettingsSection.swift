import StringsLibrary
import SwiftUI

struct UserSettingsListSection: View {
	let isAchievementsEnabled: Bool
	let unseenAchievements: Int

	var body: some View {
		Section(Strings.Settings.title) {
			SettingsLink(
				title: Strings.Opponent.List.title,
				subtitle: Strings.Settings.Opponents.footer,
				destination: SettingsList.SettingsItem.opponents
			)

			SettingsLink(
				title: Strings.Settings.Statistics.title,
				subtitle: Strings.Settings.Statistics.footer,
				destination: SettingsList.SettingsItem.statistics
			)

			if isAchievementsEnabled {
				SettingsLink(
					title: Strings.Settings.Achievements.title,
					subtitle: Strings.Settings.Achievements.footer,
					badge: unseenAchievements,
					destination: SettingsList.SettingsItem.achievements
				)
			}
		}
	}
}
