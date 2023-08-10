public enum PreferenceKey: String {

	// MARK: - App

	case appDidCompleteOnboarding                   // default: false

	// MARK: - Statistics

	case statisticsCountH2AsH                       // default: true
	case statisticsCountSplitWithBonusAsSplit       // default: true
	case statisticsHideZeroStatistics               // default: true

	// MARK: - Statistics Widgets

	case statisticsWidgetDidConfigureBowlers        // default: false
	case statisticsWidgetHideInBowlerList           // default: false

	case statisticsWidgetDidConfigureLeagues        // default: false
	case statisticsWidgetHideInLeagueList           // default: false

	// MARK: - Analytics
	case analyticsOptInStatus                       // default: "optIn"
}
