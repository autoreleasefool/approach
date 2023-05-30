import PreferenceServiceInterface
import StatisticsLibrary

extension PreferenceService {
	func perFrameConfiguration() -> TrackablePerFrameConfiguration {
		.init(countHeadPin2AsHeadPin: bool(forKey: .statisticsCountH2AsH) ?? false)
	}

	func perGameConfiguration() -> TrackablePerGameConfiguration {
		.init()
	}

	func perSeriesConfiguration() -> TrackablePerSeriesConfiguration {
		.init()
	}
}
