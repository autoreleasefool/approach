import SwiftUI
import ThemesLibrary

public struct PlaceholderWidget: View {
	let size: WidgetSize

	public init(size: WidgetSize) {
		self.size = size
	}

	public var body: some View {
		WidgetContainer(size: size) {
			VStack(alignment: .leading, spacing: .smallSpacing) {
				Text("Statistics at a glance")
					.font(.title)
					.foregroundColor(.white)

				Text("Tap here to configure the stats you want to see")
					.foregroundColor(.white)
			}
			.background(alignment: .bottomTrailing) {
				Image(uiImage: .iconAnalytics)
					.resizable()
					.scaledToFit()
					.opacity(0.4)
			}
		}
	}
}
