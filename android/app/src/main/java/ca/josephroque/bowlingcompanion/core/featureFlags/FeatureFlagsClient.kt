package ca.josephroque.bowlingcompanion.core.featureFlags

interface FeatureFlagsClient {
	fun isEnabled(flag: FeatureFlag): Boolean
	fun setEnabled(flag: FeatureFlag, enabled: Boolean)
}