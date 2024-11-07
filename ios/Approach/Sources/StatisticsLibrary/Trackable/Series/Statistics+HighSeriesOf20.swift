import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct HighSeriesOf20: HighSeriesStatistic {
		public static var title: String { Strings.Statistics.Title.highSeriesOf20 }
		public static var seriesSize: Int { 20 }
		public static var isEligibleForNewLabel: Bool { true }

		public var highSeries: Int = 0

		public init() {}
		init(highSeries: Int) { self.highSeries = highSeries }
	}
}
