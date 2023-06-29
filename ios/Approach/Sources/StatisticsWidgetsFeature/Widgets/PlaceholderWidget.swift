import AssetsLibrary
import StringsLibrary
import SwiftUI

public struct PlaceholderWidget: View {
	public init() {}

	public var body: some View {
		WidgetContainer {
			VStack(alignment: .leading, spacing: .smallSpacing) {
				Text(Strings.Statistics.Placeholder.title)
					.font(.title)
					.foregroundColor(.white)

				Text(Strings.Statistics.Placeholder.message)
					.foregroundColor(.white)
			}
			.padding(.horizontal)
			.background(alignment: .bottomTrailing) {
				Asset.Media.Icons.analytics.swiftUIImage
					.resizable()
					.scaledToFit()
					.opacity(0.4)
			}
		}
	}
}
