import AssetsLibrary
import ModelsLibrary
import StringsLibrary
import SwiftUI

extension StatisticsWidget {
	public struct PlaceholderWidget: View {
		public init() {}

		public var body: some View {
			StatisticsWidget.Container {
				VStack(alignment: .leading, spacing: .smallSpacing) {
					Text(Strings.Statistics.Placeholder.title)
						.font(.title)
						.foregroundStyle(.white)

					Text(Strings.Statistics.Placeholder.message)
						.foregroundStyle(.white)
				}
				.background(alignment: .bottomTrailing) {
					Asset.Media.Icons.analytics.swiftUIImage
						.resizable()
						.scaledToFit()
						.opacity(0.4)
				}
				.padding()
			}
		}
	}
}
