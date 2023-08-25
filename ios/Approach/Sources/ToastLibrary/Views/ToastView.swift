import AssetsLibrary
import ComposableArchitecture
import SwiftUI

public struct ToastView<Action: ToastableAction>: View {
	let toast: ToastState<Action>
	let viewStore: ViewStore<ToastState<Action>?, Action>

	public var body: some View {
		HStack(alignment: .center, spacing: .smallSpacing) {
			if let icon = toast.icon {
				Image(systemSymbol: icon)
					.resizable()
					.scaledToFit()
					.frame(width: .extraTinyIcon, height: .extraTinyIcon)
			}

			Text(toast.message)

			if let button = toast.button {
				Button {
					viewStore.send(button.action.action)
				} label: {
					Text(button.title)
						.font(.caption)
						.textCase(.uppercase)
						.padding(.smallSpacing)
						.background(
							Material.ultraThinMaterial,
							in: RoundedRectangle(cornerRadius: .smallRadius)
						)
				}
			}
		}
		.padding(.horizontal, .standardSpacing)
		.padding(.vertical, .smallSpacing)
		.foregroundColor(toast.style.foregroundColor)
		.background(
			RoundedRectangle(cornerRadius: .standardRadius)
				.fill(toast.style.backgroundColor)
		)
		.padding(.horizontal, .standardSpacing)
		.padding(.bottom, .standardSpacing)
	}
}
