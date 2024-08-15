import AssetsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

struct StrikeOutSection: View {
	let action: () -> Void

	var body: some View {
		Section {
			NavigationButton(action: action) {
				VStack(alignment: .leading, spacing: .unitSpacing) {
					Text(Strings.Game.Editor.Fields.StrikeOut.title)
						.font(.headline)
						.frame(maxWidth: .infinity, alignment: .leading)

					Text(Strings.Game.Editor.Fields.StrikeOut.subtitle)
						.font(.caption)
				}
				.padding()
				.background(
					RoundedRectangle(cornerRadius: .standardRadius)
						.fill(Color(uiColor: .secondarySystemGroupedBackground))
				)
			}
			.listRowInsets(EdgeInsets())
		}
		.listRowBackground(Color.clear)
	}
}
