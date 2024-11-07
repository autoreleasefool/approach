import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct HighSeriesOf3: HighSeriesStatistic {
		public static var title: String { Strings.Statistics.Title.highSeriesOf3 }
		public static var seriesSize: Int { 3 }

		public var highSeries: Int = 0

		public init() {}
		init(highSeries: Int) { self.highSeries = highSeries }
	}
}
