public enum PreferenceKey: String, Sendable {

	// MARK: - App

	case appDidCompleteOnboarding                   // default: false
	case appDidMigrateToSwiftUtilities              // default: false

	// MARK: - Game

	case gameShouldNotifyEditorChanges              // default: true
	case gameDidDismissDragHint                     // default: false

	// MARK: - Statistics

	case statisticsCountH2AsH                       // default: true
	case statisticsCountSplitWithBonusAsSplit       // default: true
	case statisticsHideZeroStatistics               // default: true
	case statisticsHideStatisticsDescriptions       // default: false
	case statisticsLastUsedTrackableFilterSource    // default: null

	// MARK: - Statistics Widgets

	case statisticsWidgetDidConfigureBowlers        // default: false
	case statisticsWidgetHideInBowlerList           // default: false

	case statisticsWidgetDidConfigureLeagues        // default: false
	case statisticsWidgetHideInLeagueList           // default: false

	// MARK: - Analytics
	case analyticsOptInStatus                       // default: "optIn"

	// MARK: - Data Management
	case dataLastExportDate                         // default: nil // 0
	case dataLastBackupDate                         // default: nil // 0
	case dataICloudBackupEnabled                    // default: true
}
