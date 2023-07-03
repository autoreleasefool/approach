import AssetsLibrary
import ModelsLibrary
import StatisticsChartsLibrary
import StatisticsLibrary
import StringsLibrary
import SwiftUI

extension StatisticsWidget {
	public struct Widget: View {
		let configuration: StatisticsWidget.Configuration
		let chartContent: Statistics.ChartContent?

		public init(configuration: StatisticsWidget.Configuration, chartContent: Statistics.ChartContent?) {
			self.configuration = configuration
			self.chartContent = chartContent
		}

		public var body: some View {
			switch chartContent {
			case let .averaging(data):
				StatisticsWidget.AveragingWidget(data, configuration: configuration)
			case let .counting(data):
				chartUnavailable(data.title)
			case let .percentage(data):
				chartUnavailable(data.title)
			case let .chartUnavailable(statistic):
				chartUnavailable(statistic)
			case let .dataMissing(statistic):
				dataMissing(statistic)
			case .none:
				chartUnavailable(configuration.statistic.type.title)
			}
		}

		private func chartUnavailable(_ statistic: String) -> some View {
			HStack {
				Spacer()
				VStack(alignment: .center) {
					Spacer()
					Asset.Media.Charts.error.swiftUIImage
						.resizable()
						.renderingMode(.template)
						.scaledToFit()
						.foregroundColor(Asset.Colors.Error.default)
						.frame(width: .smallIcon, height: .smallIcon)

					Text(Strings.Widget.Chart.unavailable)
					Spacer()
				}
				Spacer()
			}
		}

		private func dataMissing(_ statistic: String) -> some View {
			HStack {
				Spacer()
				VStack(alignment: .center) {
					Spacer()
					Asset.Media.Charts.noData.swiftUIImage
						.resizable()
						.renderingMode(.template)
						.scaledToFit()
						.foregroundColor(Asset.Colors.Warning.default)
						.frame(width: .smallIcon, height: .smallIcon)

					Text(Strings.Widget.Chart.noData)
					Spacer()
				}
				Spacer()
			}
		}
	}
}
