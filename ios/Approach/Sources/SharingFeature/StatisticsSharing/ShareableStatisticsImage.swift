import AssetsLibrary
import ModelsLibrary
import StatisticsChartsLibrary
import StatisticsLibrary
import StatisticsWidgetsLibrary
import StringsLibrary
import SwiftUI

public struct ShareableStatisticsImage: View {
	public let configuration: Configuration

	public init(configuration: Configuration) {
		self.configuration = configuration
	}

	public var body: some View {
		StatisticsWidget.Widget(
			configuration: configuration.widget,
			chartContent: configuration.chart
		)
		.aspectRatio(2, contentMode: .fit)
	}
}

extension ShareableStatisticsImage {
	public struct Configuration: Equatable {
		public let widget: StatisticsWidget.Configuration
		public let chart: Statistics.ChartContent
		public let displayScale: CGFloat
		public let colorScheme: ColorScheme

		public init(
			widget: StatisticsWidget.Configuration,
			chart: Statistics.ChartContent,
			displayScale: CGFloat = .zero,
			colorScheme: ColorScheme
		) {
			self.widget = widget
			self.chart = chart
			self.displayScale = displayScale
			self.colorScheme = colorScheme
		}
	}
}
