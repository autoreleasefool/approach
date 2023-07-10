extension Analytics.Statistic {
	public struct Viewed: TrackableEvent {
		public let name = "Statistic.Viewed"

		public let statisticName: String
		public let countsH2AsH: Bool
		public let countsS2AsS: Bool
		public let hidesZeroStatistics: Bool

		public var payload: [String: String]? {
			[
				"StatisticName": statisticName,
				"CountsH2AsH": String(countsH2AsH),
				"CountsS2AsS": String(countsS2AsS),
				"HidesZero": String(hidesZeroStatistics),
			]
		}

		public init(
			statisticName: String,
			countsH2AsH: Bool,
			countsS2AsS: Bool,
			hidesZeroStatistics: Bool
		) {
			self.statisticName = statisticName
			self.countsH2AsH = countsH2AsH
			self.countsS2AsS = countsS2AsS
			self.hidesZeroStatistics = hidesZeroStatistics
		}
	}
}
