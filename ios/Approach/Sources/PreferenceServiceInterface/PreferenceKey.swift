public enum PreferenceKey: String {

	// MARK: - App

	case appDidCompleteOnboarding                   // default: false
	case appSessions                                // default: 0
	case appInstallDate                             // default: 0
	case appLastReviewRequestDate                   // default: 0
	case appLastReviewVersion                       // default: ""

	// MARK: - Game

	case gameShouldNotifyEditorChanges              // default: true

	// MARK: - Statistics

	case statisticsCountH2AsH                       // default: true
	case statisticsCountSplitWithBonusAsSplit       // default: true
	case statisticsHideZeroStatistics               // default: true
	case statisticsHideStatisticsDescriptions       // default: false

	// MARK: - Statistics Widgets

	case statisticsWidgetDidConfigureBowlers        // default: false
	case statisticsWidgetHideInBowlerList           // default: false

	case statisticsWidgetDidConfigureLeagues        // default: false
	case statisticsWidgetHideInLeagueList           // default: false

	// MARK: - Analytics
	case analyticsOptInStatus                       // default: "optIn"

	// MARK: - Data Management
	case dataLastExportDate                         // default: 0
}
