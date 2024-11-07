import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct HighSeriesOf5: HighSeriesStatistic {
		public static var title: String { Strings.Statistics.Title.highSeriesOf5 }
		public static var seriesSize: Int { 5 }
		public static var isEligibleForNewLabel: Bool { true }

		public var highSeries: Int = 0

		public init() {}
		init(highSeries: Int) { self.highSeries = highSeries }
	}
}
