import StringsLibrary
import SwiftUI

struct AppearancePicker: View {
	@Binding var appearance: Appearance

	init(selection: Binding<Appearance>) {
		self._appearance = selection
	}

	var body: some View {
		Picker(
			Strings.Sharing.ColorScheme.title,
			selection: $appearance
		) {
			ForEach(Appearance.allCases) { appearance in
				Text(appearance.title)
					.tag(appearance)
			}
		}
	}
}
