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
