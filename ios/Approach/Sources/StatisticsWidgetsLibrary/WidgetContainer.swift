import AssetsLibrary
import SwiftUI

public struct WidgetContainer<Content: View>: View {
	let title: String?
	let content: Content

	init(title: String? = nil, @ViewBuilder content: () -> Content) {
		self.title = title
		self.content = content()
	}

	public var body: some View {
		VStack(alignment: .leading, spacing: .unitSpacing) {
			if let title {
				Text(title)
					.font(.subheadline)
			}

			content
				.frame(maxWidth: .infinity)
				.background(Asset.Colors.Primary.default)
		}
	}
}
