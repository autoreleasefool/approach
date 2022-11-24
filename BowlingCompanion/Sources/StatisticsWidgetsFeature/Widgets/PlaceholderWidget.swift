import SwiftUI
import ThemesLibrary

public struct PlaceholderWidget: View {
	let size: WidgetSize

	public init(size: WidgetSize) {
		self.size = size
	}

	public var body: some View {
		WidgetContainer(size: size) {
			VStack(alignment: .leading, spacing: Theme.Spacing.small) {
				Text("Statistics at a glance")
					.font(.title)
					.foregroundColor(Theme.Colors.textOnPrimary)

				Text("Tap here to configure the stats you want to see")
					.font(.body)
					.foregroundColor(Theme.Colors.textOnPrimary)
			}
			.background(alignment: .bottomTrailing) {
				Image(uiImage: Theme.Images.Icons.analytics)
					.resizable()
					.scaledToFit()
					.opacity(0.4)
			}
		}
	}
}
