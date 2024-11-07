import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct HighSeriesOf10: HighSeriesStatistic {
		public static var title: String { Strings.Statistics.Title.highSeriesOf10 }
		public static var seriesSize: Int { 10 }
		public static var isEligibleForNewLabel: Bool { true }

		public var highSeries: Int = 0

		public init() {}
		init(highSeries: Int) { self.highSeries = highSeries }
	}
}
