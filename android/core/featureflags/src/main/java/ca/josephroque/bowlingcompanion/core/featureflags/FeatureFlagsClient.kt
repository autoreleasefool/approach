package ca.josephroque.bowlingcompanion.core.featureflags

interface FeatureFlagsClient {
	fun isEnabled(flag: FeatureFlag): Boolean
	fun setEnabled(flag: FeatureFlag, enabled: Boolean?)
}
