import AssetsLibrary
import SwiftUI

struct HUDView: View {
	let title: String
	let icon: SFSymbol?
	let style: ToastStyle
	let onTap: () -> Void

	var body: some View {
		VStack {
			if let icon {
				Image(systemSymbol: icon)
					.resizable()
					.scaledToFit()
					.frame(width: .extraLargeIcon, height: .extraLargeIcon)
					.foregroundColor(style.foreground)
					.padding(.bottom, .smallSpacing)
			}

			Text(title)
				.bold()
				.foregroundColor(style.foreground)
		}
		.padding()
		.background(style.background)
		.clipShape(RoundedRectangle(cornerRadius: .standardRadius))
		.onTapGesture(perform: onTap)
	}
}
