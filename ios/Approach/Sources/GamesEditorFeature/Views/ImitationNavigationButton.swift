import AssetsLibrary
import SwiftUI
import ViewsLibrary

public struct NavigationButton<Content: View>: View {
	let action: () -> Void
	let content: Content

	init(action: @escaping () -> Void, @ViewBuilder content: () -> Content) {
		self.action = action
		self.content = content()
	}

	public var body: some View {
		Button(action: action) {
			HStack {
				content
					.frame(maxWidth: .infinity)

				Image(systemSymbol: .chevronForward)
					.resizable()
					.scaledToFit()
					.frame(width: .tinyIcon, height: .tinyIcon)
					.foregroundColor(Color(uiColor: .secondaryLabel))
			}
			.contentShape(Rectangle())
		}
		.buttonStyle(TappableElement())
	}
}

