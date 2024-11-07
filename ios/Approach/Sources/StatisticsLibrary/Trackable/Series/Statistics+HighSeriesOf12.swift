import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct HighSeriesOf12: HighSeriesStatistic {
		public static var title: String { Strings.Statistics.Title.highSeriesOf12 }
		public static var seriesSize: Int { 12 }
		public static var isEligibleForNewLabel: Bool { true }

		public var highSeries: Int = 0

		public init() {}
		init(highSeries: Int) { self.highSeries = highSeries }
	}
}
