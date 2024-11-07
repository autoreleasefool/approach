import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct HighSeriesOf4: HighSeriesStatistic {
		public static var title: String { Strings.Statistics.Title.highSeriesOf4 }
		public static var seriesSize: Int { 4 }
		public static var isEligibleForNewLabel: Bool { true }

		public var highSeries: Int = 0

		public init() {}
		init(highSeries: Int) { self.highSeries = highSeries }
	}
}
