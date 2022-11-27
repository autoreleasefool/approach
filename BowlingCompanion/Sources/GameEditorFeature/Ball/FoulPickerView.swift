import SwiftUI
import ThemesLibrary
import ViewsLibrary

struct FoulPickerView: View {
	@Binding var fouled: Bool

	var body: some View {
		Button { } label: {
			HStack(alignment: .center, spacing: .standardSpacing) {
				VStack(alignment: .trailing, spacing: .unitSpacing) {
					Text("Fouled?")
					Text(fouled ? "Yes" : "No")
						.font(.caption)
				}

				Image(systemName: "chevron.up.chevron.down")
					.resizable()
					.frame(width: .tinyIcon, height: .tinyIcon)
					.foregroundColor(.appAction)
			}
			.contentShape(Rectangle())
		}
		.buttonStyle(TappableElement())
	}
}
