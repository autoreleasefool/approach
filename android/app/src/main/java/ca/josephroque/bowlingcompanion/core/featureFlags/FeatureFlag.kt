package ca.josephroque.bowlingcompanion.core.featureFlags

import ca.josephroque.bowlingcompanion.BuildConfig

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
): Feature {
	DATA_EXPORT("DataExport", "2023-10-12", RolloutStage.DEVELOPMENT),
}

fun FeatureFlag.isEnabled(): Boolean = if (BuildConfig.DEBUG) {
	this.rolloutStage >= RolloutStage.DEVELOPMENT
} else {
		this.rolloutStage == RolloutStage.RELEASE
}