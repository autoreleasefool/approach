extension Statistics {
	public static let allCases: [any Statistic.Type] = [
		// Overall
		Statistics.HighSingle.self,

		// First Roll
		Statistics.HeadPins.self,

		// Series
		Statistics.HighSeriesOf3.self,
	]

	public static func all(forSource trackableSource: TrackableFilter.Source) -> [any Statistic.Type] {
		allCases.filter { $0.supports(trackableSource: trackableSource) }
	}
}

extension Statistics {
	public static func type(fromId: String) -> (any Statistic.Type)? {
		Self.allCases.first { $0.title == fromId }
	}
}
