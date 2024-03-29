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
}

enum class FeatureFlag(
	override val key: String,
	override val introduced: String,
	override val rolloutStage: RolloutStage,
) : Feature {
	DATA_EXPORT("DataExport", "2023-10-12", RolloutStage.RELEASE),
	DATA_IMPORT("DataImport", "2023-10-13", RolloutStage.RELEASE),
	PRE_BOWL_FORM("PreBowlForm", "2024-03-24", RolloutStage.RELEASE),
	MANUAL_SERIES_FORM("ManualSeriesForm", "2024-03-28", RolloutStage.RELEASE),
}

fun FeatureFlag.isEnabled(): Boolean = if (BuildConfig.DEBUG) {
	this.rolloutStage >= RolloutStage.DEVELOPMENT
} else {
	this.rolloutStage == RolloutStage.RELEASE
}
