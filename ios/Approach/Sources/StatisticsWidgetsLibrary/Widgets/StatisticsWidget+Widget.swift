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
				chartLoading(configuration.statistic.type.title)
			}
		}

		private func chartLoading(_ statistic: String) -> some View {
			centered {
				ProgressView()
			}
		}

		private func chartUnavailable(_ statistic: String) -> some View {
			centered {
				Asset.Media.Charts.error.swiftUIImage
					.resizable()
					.renderingMode(.template)
					.scaledToFit()
					.foregroundColor(Asset.Colors.Error.default)
					.frame(width: .smallIcon, height: .smallIcon)

				Text(Strings.Widget.Chart.unavailable)
			}
		}

		private func dataMissing(_ statistic: String) -> some View {
			centered {
				Asset.Media.Charts.noData.swiftUIImage
					.resizable()
					.renderingMode(.template)
					.scaledToFit()
					.foregroundColor(Asset.Colors.Warning.default)
					.frame(width: .smallIcon, height: .smallIcon)

				Text(Strings.Widget.Chart.noData)
			}
		}

		private func centered<Content: View>(@ViewBuilder _ content: () -> Content) -> some View {
			HStack(alignment: .center) {
				Spacer()
				VStack(alignment: .center) {
					Spacer()
					content()
					Spacer()
				}
				Spacer()
			}
			.contentShape(Rectangle())
		}
	}
}
