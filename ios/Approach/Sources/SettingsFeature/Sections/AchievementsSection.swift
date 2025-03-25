import AssetsLibrary
import StringsLibrary
import SwiftUI

struct AchievementsSection: View {
	let unseenCount: Int
	let onTapAchievementsButton: () -> Void

	var body: some View {
		Section {
			Button(action: onTapAchievementsButton) {
				HStack(spacing: 0) {
					Text(Strings.Settings.Achievements.title)

					if unseenCount > 0 {
						Spacer(minLength: .standardSpacing)

						Text("\(unseenCount)")
							.foregroundColor(.secondary)
					}
				}
			}
			.buttonStyle(.navigation)
		} footer: {
			Text(Strings.Settings.Achievements.footer)
		}
	}
}
