import Charts
import StatisticsLibrary
import SwiftUI

public struct StatisticsChart: View {
	let statistic: any GraphableStatistic.Type
	let entries: [ChartEntry]

	public init(_ entries: [ChartEntry], forStatistic: any GraphableStatistic.Type) {
		self.statistic = forStatistic
		self.entries = entries
	}

	public var body: some View {
		Chart {
			ForEach(entries) { entry in
				BarMark(
					x: .value("Date", entry.date),
					y: .value(statistic.title, entry.value.value)
				)
			}
		}
	}
}

#if DEBUG
struct ChartPreviews: PreviewProvider {
	static var previews: some View {
		StatisticsChart(
			[
				.init(value: .init(27), date: Date(timeIntervalSince1970: 1662512400)),
				.init(value: .init(46), date: Date(timeIntervalSince1970: 1663117200)),
				.init(value: .init(21), date: Date(timeIntervalSince1970: 1663722000)),
			],
			forStatistic: Statistics.HeadPins.self
		)
	}
}
#endif
