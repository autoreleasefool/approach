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
				averagingWidget(data)
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

		private func averagingWidget(_ data: AveragingChart.Data) -> some View {
			StatisticsWidget.WidgetLayout(data.title) {
				AveragingChart.Compact(
					data,
					style: .init(
						lineMarkColor: Asset.Colors.Charts.Averaging.Compact.lineMark,
						axesColor: Asset.Colors.Charts.Averaging.Compact.axes,
						hideXAxis: true,
						hideYAxis: false
					)
				)
			} footer: {
				LabeledContent(
					String(describing: configuration.timeline),
					value: format(percentageWithModifier: data.percentDifferenceOverFullTimeSpan)
				)
				.labeledContentStyle(WidgetLabeledContentStyle(
					labelColor: Asset.Colors.Charts.Averaging.Compact.axes,
					contentColor: data.percentDifferenceOverFullTimeSpan > 0
					? Asset.Colors.Charts.Averaging.Compact.positiveChange
					: Asset.Colors.Charts.Averaging.Compact.negativeChange
				))
			}
		}

		private func dataMissing(_ statistic: String) -> some View {
			StatisticsWidget.WidgetLayout(statistic, subtitle: Strings.Widget.Chart.Placeholder.notEnoughData) {
				AveragingChart.Compact(
					.createPlaceholderData(forStatistic: statistic),
					style: .init(
						lineMarkColor: Asset.Colors.Charts.Averaging.Compact.lineMark,
						axesColor: Asset.Colors.Charts.Averaging.Compact.axes,
						hideXAxis: true,
						hideYAxis: true
					)
				)
			} footer: {
				Label(Strings.Widget.Chart.Placeholder.whatDoesThisMean, systemSymbol: .questionmarkCircle)
					.font(.caption)
					.foregroundColor(.white)
			}
		}

		private func chartUnavailable(_ statistic: String) -> some View {
			StatisticsWidget.WidgetLayout(statistic, subtitle: Strings.Widget.Chart.unavailable) {
				centered {
					Asset.Media.Charts.error.swiftUIImage
						.resizable()
						.renderingMode(.template)
						.scaledToFit()
						.foregroundColor(Asset.Colors.Error.default)
						.frame(width: .smallIcon, height: .smallIcon)
						.padding()
				}
			} footer: {
				Label(Strings.Widget.Chart.Placeholder.whatDoesThisMean, systemSymbol: .questionmarkCircle)
					.font(.caption)
					.foregroundColor(.white)
			}
		}

		private func chartLoading(_ statistic: String) -> some View {
			centered {
				ProgressView()
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

#if DEBUG
struct StatisticsWidgetPreview: PreviewProvider {
	static var previews: some View {
		LazyVGrid(
			columns: [.init(spacing: .standardSpacing), .init(spacing: .standardSpacing)],
			spacing: .standardSpacing
		) {
			StatisticsWidget.Widget(
				configuration: .init(id: UUID(0), source: .bowler(UUID(0)), timeline: .allTime, statistic: .average),
				chartContent: .averaging(AveragingChart.Data.bowlerAverageDecrementingMock)
			)
			StatisticsWidget.Widget(
				configuration: .init(id: UUID(0), source: .bowler(UUID(0)), timeline: .allTime, statistic: .average),
				chartContent: .averaging(AveragingChart.Data.bowlerAverageIncrementingMock)
			)
			StatisticsWidget.Widget(
				configuration: .init(id: UUID(0), source: .bowler(UUID(0)), timeline: .allTime, statistic: .average),
				chartContent: .dataMissing(statistic: "Average")
			)
			StatisticsWidget.Widget(
				configuration: .init(id: UUID(0), source: .bowler(UUID(0)), timeline: .allTime, statistic: .average),
				chartContent: nil
			)
			StatisticsWidget.Widget(
				configuration: .init(id: UUID(0), source: .bowler(UUID(0)), timeline: .allTime, statistic: .average),
				chartContent: .chartUnavailable(statistic: "Average")
			)

		}
	}
}
#endif
