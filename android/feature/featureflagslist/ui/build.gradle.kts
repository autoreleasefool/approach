plugins {
	id("approach.android.feature.ui")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.featureflagslist.ui"
}

dependencies {
	implementation(projects.core.featureflags)
}
