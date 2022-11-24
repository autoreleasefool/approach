import SwiftUI
import ThemesLibrary

struct WidgetContainer<Content: View>: View {
	let title: String?
	let size: WidgetSize
	let content: Content

	init(title: String? = nil, size: WidgetSize, @ViewBuilder content: () -> Content) {
		self.title = title
		self.size = size
		self.content = content()
	}

	var body: some View {
		VStack(alignment: .leading, spacing: Theme.Spacing.unit) {
			if let title {
				Text(title)
					.font(.subheadline)
			}

			content
				.padding(.vertical)
				.frame(maxWidth: .infinity)
				.background(Theme.Colors.primary)
				.cornerRadius(Theme.Spacing.standard)
		}
	}
}
