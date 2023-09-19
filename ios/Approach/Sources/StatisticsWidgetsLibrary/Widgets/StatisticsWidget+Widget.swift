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
				countingWidget(data)
			case let .percentage(data):
				percentageWidget(data)
			case let .chartUnavailable(statistic):
				chartUnavailable(statistic)
			case let .dataMissing(statistic):
				dataMissing(statistic)
			case .none:
				chartLoading(configuration.statistic)
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

		private func percentageWidget(_ data: PercentageChart.Data) -> some View {
			StatisticsWidget.WidgetLayout(data.title) {
				PercentageChart.Compact(
					data,
					style: .init(
						barMarkColor: Asset.Colors.Charts.Percentage.Compact.barMark,
						denominatorLineMarkColor: Asset.Colors.Charts.Percentage.Compact.denominatorLineMark,
						numeratorLineMarkColor: Asset.Colors.Charts.Percentage.Compact.numeratorLineMark,
						axesColor: Asset.Colors.Charts.Percentage.Compact.axes,
						hideXAxis: true
					)
				)
			} footer: {
				if let percentageDifferenceOverTime = data.percentDifferenceOverFullTimeSpan {
					LabeledContent(
						String(describing: configuration.timeline),
						value: format(percentageWithModifier: percentageDifferenceOverTime)
					)
					.labeledContentStyle(WidgetLabeledContentStyle(
						labelColor: Asset.Colors.Charts.Percentage.Compact.axes,
						contentColor: percentageDifferenceOverTime > 0
							? Asset.Colors.Charts.Percentage.Compact.positiveChange
							: Asset.Colors.Charts.Percentage.Compact.negativeChange
					))
				} else {
					Text(String(describing: configuration.timeline))
						.foregroundColor(Asset.Colors.Charts.Percentage.Compact.axes)
						.font(.caption)
				}
			}
		}

		private func countingWidget(_ data: CountingChart.Data) -> some View {
			StatisticsWidget.WidgetLayout(data.title) {
				CountingChart.Compact(
					data,
					style: .init(
						areaMarkColor: Asset.Colors.Charts.Counting.Compact.areaMark,
						barMarkColor: Asset.Colors.Charts.Counting.Compact.barMark,
						lineMarkColor: Asset.Colors.Charts.Counting.Compact.lineMark,
						axesColor: Asset.Colors.Charts.Counting.Compact.axes,
						hideXAxis: true
					)
				)
			} footer: {
				Text(String(describing: configuration.timeline))
					.foregroundColor(Asset.Colors.Charts.Counting.Compact.axes)
					.font(.caption)
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
				whatDoesThisMeanFooter
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
				whatDoesThisMeanFooter
			}
		}

		private func chartLoading(_ statistic: String) -> some View {
			centered {
				ProgressView()
			}
		}

		private var whatDoesThisMeanFooter: some View {
			HStack(spacing: .standardSpacing) {
				Image(systemSymbol: .questionmarkCircle)
					.resizable()
					.scaledToFit()
					.foregroundColor(.white)
					.frame(width: .tinyIcon, height: .tinyIcon)

				Text(Strings.Widget.Chart.Placeholder.whatDoesThisMean)
					.font(.caption)
					.foregroundColor(.white)
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
				configuration: .init(id: UUID(0), bowlerId: UUID(0), leagueId: nil, timeline: .allTime, statistic: "Average"),
				chartContent: .averaging(AveragingChart.Data.bowlerAverageDecrementingMock)
			)
			StatisticsWidget.Widget(
				configuration: .init(id: UUID(0), bowlerId: UUID(0), leagueId: nil, timeline: .allTime, statistic: "Middle Hits"),
				chartContent: .counting(CountingChart.Data.bowlerHeadPinsMock)
			)
			StatisticsWidget.Widget(
				configuration: .init(id: UUID(0), bowlerId: UUID(0), leagueId: nil, timeline: .allTime, statistic: "Middle Hits"),
				chartContent: .percentage(PercentageChart.Data.bowlerMiddleHitsMock)
			)
			StatisticsWidget.Widget(
				configuration: .init(id: UUID(0), bowlerId: UUID(0), leagueId: nil, timeline: .allTime, statistic: "Average"),
				chartContent: .dataMissing(statistic: "Average")
			)
			StatisticsWidget.Widget(
				configuration: .init(id: UUID(0), bowlerId: UUID(0), leagueId: nil, timeline: .allTime, statistic: "Average"),
				chartContent: nil
			)
			StatisticsWidget.Widget(
				configuration: .init(id: UUID(0), bowlerId: UUID(0), leagueId: nil, timeline: .allTime, statistic: "Average"),
				chartContent: .chartUnavailable(statistic: "Average")
			)

		}
	}
}
#endif
