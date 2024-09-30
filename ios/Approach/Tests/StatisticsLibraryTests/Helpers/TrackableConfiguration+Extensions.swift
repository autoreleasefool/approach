import StatisticsLibrary

extension TrackablePerSeriesConfiguration {
	static var `default`: Self { Self() }
}

extension TrackablePerGameConfiguration {
	static var `default`: Self { Self() }
}

extension TrackablePerFrameConfiguration {
	static var `default`: Self { Self(countHeadPin2AsHeadPin: false, countSplitWithBonusAsSplit: false) }
}
