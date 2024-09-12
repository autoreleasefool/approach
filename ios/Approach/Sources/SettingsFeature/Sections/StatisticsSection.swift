import StringsLibrary
import SwiftUI

struct StatisticsSection: View {
	let onTapStatisticsButton: () -> Void

	var body: some View {
		Section {
			Button(action: onTapStatisticsButton) {
				Text(Strings.Settings.Statistics.title)
			}
			.buttonStyle(.navigation)
		} footer: {
			Text(Strings.Settings.Statistics.footer)
		}
	}
}
