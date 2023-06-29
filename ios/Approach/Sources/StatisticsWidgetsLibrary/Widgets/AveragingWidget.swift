import AssetsLibrary
import StatisticsChartsLibrary
import StatisticsChartsMocksLibrary
import StringsLibrary
import SwiftUI

public struct AveragingWidget: View {
	let data: AveragingChart.Data
	let configuration: WidgetConfiguration

	public init(_ data: AveragingChart.Data, configuration: WidgetConfiguration) {
		self.data = data
		self.configuration = configuration
	}

	public var body: some View {
		WidgetContainer {
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

#if DEBUG
struct AveragingWidgetPreview: PreviewProvider {
	static var previews: some View {
		Grid {
			GridRow {
				AveragingWidget(
					AveragingChart.Data.bowlerAverageIncrementingMock,
					configuration: .init(timeline: .past1Month)
				)
				.cornerRadius(.largeRadius)
				.aspectRatio(1, contentMode: .fit)

				AveragingWidget(
					AveragingChart.Data.bowlerAverageDecrementingMock,
					configuration: .init(timeline: .allTime)
				)
				.cornerRadius(.largeRadius)
				.aspectRatio(1, contentMode: .fit)
			}
		}
		.padding()
	}
}
#endif
