extension Statistics {
	public static let allCases: [Statistic.Type] = [
		// Overall
		Statistics.GameAverage.self,
		Statistics.HighSingle.self,

		// First Roll
		Statistics.HeadPins.self,

		// Series
		Statistics.HighSeriesOf3.self,
	]

	public static func all(forSource trackableSource: TrackableFilter.Source) -> [Statistic.Type] {
		allCases.filter { $0.supports(trackableSource: trackableSource) }
	}
}
