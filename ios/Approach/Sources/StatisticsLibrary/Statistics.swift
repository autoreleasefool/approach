extension Statistics {
	public static let all: [any Statistic.Type] = [
		// Overall
		Statistics.HighSingle.self,

		// First Roll
		Statistics.HeadPins.self,

		// Series
		Statistics.HighSeriesOf3.self,
	]
}
