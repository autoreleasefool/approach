import AssetsLibrary
import ComposableArchitecture
import SwiftUI

public struct HUDView<Action: ToastableAction>: View {
	let toast: ToastState<Action>
	let viewStore: ViewStore<ToastState<Action>?, Action>

	public var body: some View {
		VStack(spacing: .standardSpacing) {
			if let icon = toast.icon {
				Image(systemSymbol: icon)
					.resizable()
					.scaledToFit()
					.frame(width: .standardIcon, height: .standardIcon)
			}

			Text(toast.message)
				.font(.headline)
		}
		.padding(.largeSpacing)
		.foregroundColor(toast.style.foregroundColor)
		.background(
			RoundedRectangle(cornerRadius: .standardRadius)
				.fill(toast.style.backgroundColor.opacity(0.6))
		)
		.padding(.standardSpacing)
	}
}
