import ModelsLibrary

// MARK: - Frame

public protocol TrackablePerFrame: Statistic {
	mutating func adjust(byFrame: Frame.TrackableEntry, configuration: TrackablePerFrameConfiguration)
}

public struct TrackablePerFrameConfiguration {
	public let countHeadPin2AsHeadPin: Bool

	public init(countHeadPin2AsHeadPin: Bool) {
		self.countHeadPin2AsHeadPin = countHeadPin2AsHeadPin
	}
}

// MARK: - Game

public protocol TrackablePerGame: Statistic {
	mutating func adjust(byGame: Game.TrackableEntry, configuration: TrackablePerGameConfiguration)
}

public struct TrackablePerGameConfiguration {
	public init() {}
}

// MARK: - Series

public protocol TrackablePerSeries: Statistic {
	mutating func adjust(bySeries: Series.TrackableEntry, configuration: TrackablePerSeriesConfiguration)
}

public struct TrackablePerSeriesConfiguration {
	public init() {}
}
