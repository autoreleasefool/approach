import StringsLibrary
import SwiftUI

struct OpponentsSection: View {
	let onTapOpponentsButton: () -> Void

	var body: some View {
		Section {
			Button(action: onTapOpponentsButton) {
				Text(Strings.Opponent.List.title)
			}
			.buttonStyle(.navigation)
		} footer: {
			Text(Strings.Settings.Opponents.footer)
		}
	}
}
