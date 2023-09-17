import AssetsLibrary
import ModelsLibrary
import StatisticsChartsLibrary
import StatisticsChartsMocksLibrary
import StringsLibrary
import SwiftUI

extension StatisticsWidget {
	public struct WidgetLayout<Content: View, Footer: View>: View {
		let title: String
		let subtitle: String?
		let content: Content
		let footer: Footer

		public init(
			_ title: String,
			subtitle: String? = nil,
			@ViewBuilder content: () -> Content,
			@ViewBuilder footer: () -> Footer
		) {
			self.title = title
			self.subtitle = subtitle
			self.content = content()
			self.footer = footer()
		}

		public var body: some View {
			StatisticsWidget.Container {
				VStack(alignment: .leading, spacing: 0) {
					Text(title)
						.font(.subheadline)
						.fontWeight(.black)
						.foregroundColor(.white)

					if let subtitle {
						Text(subtitle)
							.font(.caption)
							.foregroundColor(.white)
							.padding(.top, .tinySpacing)
					}

					content
						.padding(.vertical, .unitSpacing)
					footer
				}
				.padding(.standardSpacing)
			}
		}
	}
}
