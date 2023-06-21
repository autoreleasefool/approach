extension Statistics {
	public static let allCases: [Statistic.Type] = [
		// Overall
		Statistics.HighSingle.self,
		Statistics.TotalPinfall.self,
		Statistics.NumberOfGames.self,
		Statistics.GameAverage.self,
		Statistics.MiddleHits.self,
		Statistics.LeftOfMiddleHits.self,
		Statistics.RightOfMiddleHits.self,

		// First Roll
		Statistics.HeadPins.self,

		// Series
		Statistics.HighSeriesOf3.self,
	]

	public static func all(forSource trackableSource: TrackableFilter.Source) -> [Statistic.Type] {
		allCases.filter { $0.supports(trackableSource: trackableSource) }
	}

	public static func type(of id: String) -> Statistic.Type? {
		Self.allCases.first { $0.title == id }
	}
}
