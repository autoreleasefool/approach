import AssetsLibrary
import StatisticsChartsLibrary
import StatisticsChartsMocksLibrary
import StringsLibrary
import SwiftUI

extension StatisticsWidget {
	public struct AveragingWidget: View {
		let data: AveragingChart.Data
		let configuration: StatisticsWidget.Configuration

		public init(_ data: AveragingChart.Data, configuration: StatisticsWidget.Configuration) {
			self.data = data
			self.configuration = configuration
		}

		public var body: some View {
			StatisticsWidget.Container {
				VStack(alignment: .leading, spacing: 0) {
					Text(data.title)
						.font(.subheadline)
						.fontWeight(.black)
						.foregroundColor(.white)

					AveragingChart.Compact(
						data,
						style: .init(
							lineMarkColor: Asset.Colors.Charts.Averaging.Compact.lineMark,
							axesColor: Asset.Colors.Charts.Averaging.Compact.axes,
							hideXAxis: true
						)
					)
					.padding(.vertical, .unitSpacing)

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
				.padding(.standardSpacing)
			}
		}
	}
}

#if DEBUG
struct AveragingWidgetPreview: PreviewProvider {
	static var previews: some View {
		Grid {
			GridRow {
				StatisticsWidget.AveragingWidget(
					AveragingChart.Data.bowlerAverageIncrementingMock,
					configuration: .init(source: .bowler(.init()), timeline: .past1Month, statistic: .average)
				)
				.cornerRadius(.largeRadius)
				.aspectRatio(1, contentMode: .fit)

				StatisticsWidget.AveragingWidget(
					AveragingChart.Data.bowlerAverageDecrementingMock,
					configuration: .init(source: .bowler(.init()), timeline: .allTime, statistic: .average)
				)
				.cornerRadius(.largeRadius)
				.aspectRatio(1, contentMode: .fit)
			}
		}
		.padding()
	}
}
#endif
