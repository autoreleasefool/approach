import StatisticsLibrary

extension TrackablePerSeriesConfiguration {
	static var `default` = Self()
}

extension TrackablePerGameConfiguration {
	static var `default` = Self()
}

extension TrackablePerFrameConfiguration {
	static var `default` = Self(countHeadPin2AsHeadPin: false, countSplitWithBonusAsSplit: false)
}
