import StringsLibrary
import SwiftUI

struct AppIconSection: View {
	let appIconImage: UIImage
	let onTapAppIconButton: () -> Void

	var body: some View {
		Section {
			Button(action: onTapAppIconButton) {
				AppIconView(
					Strings.Settings.AppIcon.title,
					icon: .image(appIconImage),
					isCompact: true
				)
			}
			.buttonStyle(.navigation)
		}
	}
}
