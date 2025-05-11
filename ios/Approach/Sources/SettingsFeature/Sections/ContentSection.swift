import StringsLibrary
import SwiftUI

struct ContentSection: View {
	let onTapOpponentsButton: () -> Void
	let onTapStatisticsButton: () -> Void

	var body: some View {
		Section {
			SettingsButton(
				title: Strings.Opponent.List.title,
				subtitle: Strings.Settings.Opponents.footer,
				action: onTapOpponentsButton
			)

			SettingsButton(
				title: Strings.Settings.Statistics.title,
				subtitle: Strings.Settings.Statistics.footer,
				action: onTapStatisticsButton
			)
		}
	}
}

#Preview {
	List {
		ContentSection(
			onTapOpponentsButton: {},
			onTapStatisticsButton: {}
		)
	}
}
