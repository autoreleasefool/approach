package ca.josephroque.bowlingcompanion.core.featureflags

import javax.inject.Inject

class OverridableFeatureFlagsClient @Inject constructor() : FeatureFlagsClient {
	private val flagOverrides: MutableMap<String, Boolean> = mutableMapOf()

	override fun isEnabled(flag: FeatureFlag): Boolean = if (BuildConfig.DEBUG) {
		flagOverrides[flag.key] ?: flag.isEnabled()
	} else {
		flag.isEnabled()
	}

	override fun setEnabled(flag: FeatureFlag, enabled: Boolean?) {
		if (!flag.isOverridable) return
		enabled?.let { flagOverrides[flag.key] = it } ?: flagOverrides.remove(flag.key)
	}
}
