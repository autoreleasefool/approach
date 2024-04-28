extension Analytics.Statistic {
	public struct Shared: TrackableEvent {
		public let name = "Statistic.Shared"

		public let statisticName: String

		public var payload: [String: String]? {
			[
				"StatisticName": statisticName,
			]
		}

		public init(statisticName: String) {
			self.statisticName = statisticName
		}
	}
}
