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
	MANUAL_SERIES_FORM("ManualSeriesForm", "2024-03-28", RolloutStage.RELEASE),
	MANUAL_TEAM_SERIES_FORM("ManualTeamSeriesForm", "2024-08-30", RolloutStage.DEVELOPMENT),
	MOVING_SERIES_BETWEEN_LEAGUES("MovingSeriesBetweenLeagues", "2025-06-22", RolloutStage.RELEASE),
	PRE_BOWL_FORM("PreBowlForm", "2024-03-24", RolloutStage.RELEASE),
	REORDER_GAMES("ReorderGames", "2025-07-13", RolloutStage.RELEASE),
	SHARING_GAMES("SharingGames", "2024-11-03", RolloutStage.RELEASE),
	SHARING_SERIES("SharingSeries", "2024-11-03", RolloutStage.RELEASE),
}

internal fun FeatureFlag.isEnabled(): Boolean = if (BuildConfig.DEBUG) {
	this.rolloutStage >= RolloutStage.DEVELOPMENT
} else {
	this.rolloutStage == RolloutStage.RELEASE
}
