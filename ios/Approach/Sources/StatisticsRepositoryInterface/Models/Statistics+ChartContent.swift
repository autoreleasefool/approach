import StatisticsChartsLibrary
import StatisticsLibrary
import StringsLibrary

extension Statistics {
	public enum ChartContent: Equatable {
		case averaging(AveragingChart.Data)
		case counting(CountingChart.Data)
		case percentage(PercentageChart.Data)
		case chartUnavailable(statistic: String)

		public var title: String {
			switch self {
			case let .averaging(data): return data.title
			case let .counting(data): return data.title
			case let .percentage(data): return data.title
			case let .chartUnavailable(statistic): return statistic
			}
		}

		public var showsAggregationPicker: Bool {
			Statistics.type(of: title)?.supportsAggregation ?? false
		}
	}
}
