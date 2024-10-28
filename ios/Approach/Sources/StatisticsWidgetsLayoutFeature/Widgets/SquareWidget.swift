import AssetsLibrary
import ModelsLibrary
import StatisticsChartsLibrary
import StatisticsLibrary
import StatisticsWidgetsLibrary
import SwiftUI
import ViewsLibrary

public struct SquareWidget: View {
	let configuration: StatisticsWidget.Configuration
	let chartContent: Statistics.ChartContent?
	let onPress: () -> Void

	public var body: some View {
		Button(action: onPress) {
			StatisticsWidget.Widget(configuration: configuration, chartContent: chartContent)
				.aspectRatio(1, contentMode: .fit)
				.cornerRadius(.standardRadius)
		}
		.buttonStyle(TappableElement())
	}
}

public struct RectangleWidget: View {
	let configuration: StatisticsWidget.Configuration
	let chartContent: Statistics.ChartContent?
	let onPress: () -> Void

	public var body: some View {
		Button(action: onPress) {
			StatisticsWidget.Widget(configuration: configuration, chartContent: chartContent)
				.aspectRatio(2, contentMode: .fit)
				.cornerRadius(.standardRadius)
		}
		.buttonStyle(TappableElement())
	}
}

#if DEBUG
#Preview {
	VStack(spacing: .standardSpacing) {
		LazyVGrid(
			columns: [.init(spacing: .standardSpacing), .init(spacing: .standardSpacing)],
			spacing: .standardSpacing
		) {
			SquareWidget(
				configuration: .init(id: UUID(0), bowlerId: UUID(0), leagueId: nil, timeline: .allTime, statistic: "Average"),
				chartContent: .averaging(AveragingChart.Data.bowlerAverageIncrementingMock)
			) {}
			SquareWidget(
				configuration: .init(id: UUID(0), bowlerId: UUID(0), leagueId: nil, timeline: .allTime, statistic: "Average"),
				chartContent: .averaging(AveragingChart.Data.bowlerAverageIncrementingMock)
			) {}
		}
		RectangleWidget(
			configuration: .init(id: UUID(0), bowlerId: UUID(0), leagueId: nil, timeline: .allTime, statistic: "Average"),
			chartContent: .averaging(AveragingChart.Data.bowlerAverageIncrementingMock)
		) {}
	}
}
#endif
