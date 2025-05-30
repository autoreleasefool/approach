import SwiftUI

struct ToastView: View {
	let title: String
	let action: String?
	let style: ToastStyle
	let onAction: () -> Void
	let onDismiss: () -> Void

	var body: some View {
		VStack {
			Spacer()

			HStack(alignment: .center, spacing: 0) {
				Text(title)
					.bold()
					.foregroundStyle(style.foreground)

				if let action {
					Spacer(minLength: .standardSpacing)

					Button(action: onAction) {
						Text(action.uppercased())
							.font(.caption)
							.foregroundStyle(style.foreground)
							.padding()
							.overlay(
								RoundedRectangle(cornerRadius: .smallRadius)
									.fill(Color.clear)
									.strokeBorder(Color.white.opacity(0.4), lineWidth: 1)
							)
					}
				}
			}
			.padding()
			.background(
				RoundedRectangle(cornerRadius: .largeRadius)
					.fill(style.background.swiftUIColor)
					.shadow(radius: .smallRadius)
			)
			.padding()
			.onTapGesture {
				onDismiss()
			}
		}
	}
}
