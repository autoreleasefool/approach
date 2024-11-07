import ModelsLibrary

public protocol HighSeriesStatistic: Statistic, TrackablePerSeries, HighestOfStatistic {
	static var seriesSize: Int { get }
	var highSeries: Int { get set }
}

extension HighSeriesStatistic {
	public static var category: StatisticCategory { .series }
	public static var isEligibleForNewLabel: Bool { false }
	public static var preferredTrendDirection: StatisticTrendDirection? { .upwards }

	public var highest: Int {
		get { highSeries }
		set { highSeries = newValue }
	}

	public mutating func adjust(bySeries: Series.TrackableEntry, configuration: TrackablePerSeriesConfiguration) {
		guard bySeries.numberOfGames == Self.seriesSize else { return }
		highest = max(highest, bySeries.total)
	}

	public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
		switch trackableSource {
		case .bowler, .league: true
		case .series, .game: false
		}
	}
}
