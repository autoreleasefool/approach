plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.seriesform"
}

dependencies {
	implementation(projects.core.featureflags)
	implementation(projects.feature.seriesform.ui)
	implementation(libs.kotlinx.datetime)
}
