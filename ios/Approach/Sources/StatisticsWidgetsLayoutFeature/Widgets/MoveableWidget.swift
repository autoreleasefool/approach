import AssetsLibrary
import ModelsLibrary
import StatisticsChartsLibrary
import StatisticsLibrary
import StatisticsWidgetsLibrary
import SwiftUI
import ViewsLibrary

public struct MoveableWidget: View {
	let configuration: StatisticsWidget.Configuration
	let chartContent: Statistics.ChartContent?
	let isWiggling: Binding<Bool>
	let isShowingDelete: Binding<Bool>
	let onDelete: () -> Void

	init(
		configuration: StatisticsWidget.Configuration,
		chartContent: Statistics.ChartContent?,
		isWiggling: Binding<Bool>,
		isShowingDelete: Binding<Bool>,
		onDelete: @escaping () -> Void
	) {
		self.configuration = configuration
		self.chartContent = chartContent
		self.isWiggling = isWiggling
		self.isShowingDelete = isShowingDelete
		self.onDelete = onDelete
	}

	public var body: some View {
		StatisticsWidget.Widget(configuration: configuration, chartContent: chartContent)
			.aspectRatio(1, contentMode: .fit)
			.cornerRadius(.standardRadius)
			.wiggling(isWiggling: isWiggling)
			.overlay(alignment: .topTrailing) {
				if isShowingDelete.wrappedValue {
					Button { onDelete() } label: {
						ZStack(alignment: .center) {
							Circle()
								.fill(Asset.Colors.Destructive.default.swiftUIColor)
								.frame(width: .smallerIcon, height: .smallerIcon)

							Image(systemName: "xmark")
								.resizable()
								.scaledToFit()
								.frame(width: .tinyIcon, height: .tinyIcon)
								.foregroundColor(.white)
						}
						.padding(.top, (.standardSpacing + .smallSpacing) * -1)
						.padding(.trailing, (.standardSpacing + .smallSpacing) * -1)
						.padding(.standardSpacing)
					}
					.frame(alignment: .topTrailing)
					.wiggling(isWiggling: isWiggling)
				}
			}
	}
}
