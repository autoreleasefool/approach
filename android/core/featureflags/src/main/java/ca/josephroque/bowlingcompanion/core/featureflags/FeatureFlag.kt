package ca.josephroque.bowlingcompanion.core.featureflags

@Suppress("unused")
enum class RolloutStage {
	DISABLED,
	DEVELOPMENT,
	TEST,
	RELEASE,
}

interface Feature {
	val key: String
	val introduced: String
	val rolloutStage: RolloutStage
	val isOverridable: Boolean
}

enum class FeatureFlag(
	override val key: String,
	override val introduced: String,
	override val rolloutStage: RolloutStage,
	override val isOverridable: Boolean = true,
) : Feature {
	DEVELOPER_MODE("DevelopmentMode", "2024-08-23", RolloutStage.DEVELOPMENT, isOverridable = false),
	DATA_EXPORT("DataExport", "2023-10-12", RolloutStage.RELEASE),
	DATA_IMPORT("DataImport", "2023-10-13", RolloutStage.RELEASE),
	HIGHEST_SCORE_POSSIBLE("HighestScorePossible", "2024-08-14", RolloutStage.DEVELOPMENT),
	MANUAL_SERIES_FORM("ManualSeriesForm", "2024-03-28", RolloutStage.RELEASE),
	MANUAL_TEAM_SERIES_FORM("ManualTeamSeriesForm", "2024-08-30", RolloutStage.DEVELOPMENT),
	PRE_BOWL_FORM("PreBowlForm", "2024-03-24", RolloutStage.RELEASE),
	SHARING_GAMES("SharingGames", "2024-11-03", RolloutStage.DEVELOPMENT),
	SHARING_SERIES("SharingSeries", "2024-11-03", RolloutStage.DEVELOPMENT),
	TEAMS("Teams", "2024-08-16", RolloutStage.RELEASE),
}

internal fun FeatureFlag.isEnabled(): Boolean = if (BuildConfig.DEBUG) {
	this.rolloutStage >= RolloutStage.DEVELOPMENT
} else {
	this.rolloutStage == RolloutStage.RELEASE
}
