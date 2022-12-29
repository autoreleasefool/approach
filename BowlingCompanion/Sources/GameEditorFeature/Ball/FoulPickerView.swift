import StringsLibrary
import SwiftUI
import AssetsLibrary
import ViewsLibrary

struct FoulPickerView: View {
	@Binding var fouled: Bool

	var body: some View {
		Button { } label: {
			HStack(alignment: .center, spacing: .standardSpacing) {
				VStack(alignment: .trailing, spacing: .unitSpacing) {
					Text(Strings.Ball.Properties.fouled)
					Text(fouled ? Strings.yes : Strings.no)
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
