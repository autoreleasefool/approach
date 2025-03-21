import StringsLibrary
import SwiftUI

struct AchievementsSection: View {
	let onTapAchievementsButton: () -> Void

	var body: some View {
		Section {
			Button(action: onTapAchievementsButton) {
				Text(Strings.Settings.Achievements.title)
			}
			.buttonStyle(.navigation)
		} footer: {
			Text(Strings.Settings.Achievements.footer)
		}
	}
}
