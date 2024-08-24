plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.featureflagslist"
}

dependencies {
	implementation(projects.core.featureflags)
	implementation(projects.feature.featureflagslist.ui)
}
