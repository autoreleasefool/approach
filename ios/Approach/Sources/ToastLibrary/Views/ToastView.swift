import AssetsLibrary
import ComposableArchitecture
import SwiftUI

public struct ToastContent<Action: ToastableAction> {
	public let message: TextState
	public let icon: SFSymbol?
	public let button: ToastState<Action>.Button?

	public init(message: TextState, icon: SFSymbol? = nil, button: ToastState<Action>.Button? = nil) {
		self.message = message
		self.icon = icon
		self.button = button
	}
}

extension ToastContent: Equatable where Action: Equatable {}

public struct ToastView<Action: ToastableAction>: View {
	let content: ToastContent<Action>
	let style: ToastStyle
	let onPressButton: () -> Void

	public var body: some View {
		HStack(alignment: .center, spacing: .smallSpacing) {
			if let icon = content.icon {
				Image(systemSymbol: icon)
					.resizable()
					.scaledToFit()
					.frame(width: .extraTinyIcon, height: .extraTinyIcon)
			}

			Text(content.message)

			if let button = content.button {
				Button(action: onPressButton) {
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
		.foregroundColor(style.foregroundColor)
		.background(
			RoundedRectangle(cornerRadius: .standardRadius)
				.fill(style.backgroundColor.swiftUIColor)
		)
		.padding(.horizontal, .standardSpacing)
		.padding(.bottom, .standardSpacing)
	}
}

#if DEBUG
private enum PreviewToastAction: ToastableAction {
	case didDismiss, didFinishDismissing
}

struct ToastViewPreview: PreviewProvider {
	static var previews: some View {
		ToastView(
			content: .init(
				message: .init("Toast!"),
				icon: .exclamationmarkCircle,
				button: .init(title: .init("Done"), action: .init(action: PreviewToastAction.didDismiss))
			),
			style: .success,
			onPressButton: {}
		)
	}
}
#endif
