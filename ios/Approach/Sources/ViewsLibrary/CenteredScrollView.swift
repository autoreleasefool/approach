import SwiftUI

public struct CenteredScrollView<Content: View>: View {
	@ViewBuilder let content: Content

	public init(@ViewBuilder content: @escaping () -> Content) {
		self.content = content()
	}

	public var body: some View {
		GeometryReader { proxy in
			ScrollView {
				content
					.frame(width: proxy.size.width)
					.frame(minHeight: proxy.size.height)
			}
		}
	}
}
