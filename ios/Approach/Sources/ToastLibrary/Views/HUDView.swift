import AssetsLibrary
import SwiftUI

struct HUDView: View {
	let title: String
	let message: String?
	let action: String?
	let systemImage: String?
	let style: ToastStyle
	let onAction: () -> Void
	let onDismiss: () -> Void

	var body: some View {
		VStack(spacing: 0) {
			if let systemImage {
				Image(systemName: systemImage)
					.resizable()
					.scaledToFit()
					.frame(width: .extraLargeIcon, height: .extraLargeIcon)
					.foregroundStyle(style.foreground)
					.padding(.bottom, .standardSpacing)
			}

			Text(title)
				.bold()
				.foregroundStyle(style.foreground)

			if let message {
				Text(message)
					.foregroundStyle(style.foreground)
					.padding(.top, .smallSpacing)
			}

			if let action {
				// Force HUD to fill width when an action is present
				HStack { Spacer() }

				Button(action: onAction) {
					Text(action)
						.frame(maxWidth: .infinity, alignment: .center)
						.font(.caption)
						.foregroundStyle(style.foreground)
						.padding()
						.overlay(
							RoundedRectangle(cornerRadius: .smallRadius)
								.fill(Color.clear)
								.strokeBorder(Color.white.opacity(0.4), lineWidth: 1)
						)
				}
				.padding(.top, .standardSpacing)
			}
		}
		.frame(maxWidth: 500)
		.padding()
		.background(style.background)
		.clipShape(RoundedRectangle(cornerRadius: .standardRadius))
		.onTapGesture(perform: onDismiss)
		.padding()
	}
}
