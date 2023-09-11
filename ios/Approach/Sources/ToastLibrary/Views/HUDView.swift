import AssetsLibrary
import ComposableArchitecture
import SwiftUI

public struct HUDContent: Equatable {
	public let message: TextState
	public let icon: SFSymbol?

	public init(message: TextState, icon: SFSymbol?) {
		self.message = message
		self.icon = icon
	}
}

public struct HUDView: View {
	let content: HUDContent
	let style: ToastStyle

	public var body: some View {
		VStack(spacing: .standardSpacing) {
			if let icon = content.icon {
				Image(systemSymbol: icon)
					.resizable()
					.scaledToFit()
					.frame(width: .standardIcon, height: .standardIcon)
			}

			Text(content.message)
				.font(.headline)
		}
		.padding(.largeSpacing)
		.foregroundColor(style.foreground)
		.background(
			RoundedRectangle(cornerRadius: .standardRadius)
				.fill(style.background.swiftUIColor.opacity(0.6))
		)
		.padding(.standardSpacing)
	}
}

#if DEBUG
struct HUDViewPreview: PreviewProvider {
	static var previews: some View {
		HUDView(
			content: .init(
				message: .init("Toast!"),
				icon: .exclamationmarkCircle
			),
			style: .success
		)
	}
}
#endif
