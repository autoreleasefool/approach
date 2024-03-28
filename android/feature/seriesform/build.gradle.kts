plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.seriesform"
}

dependencies {
	implementation(project(":core:featureflags"))
	implementation(project(":feature:seriesform:ui"))
	implementation(libs.kotlinx.datetime)
}
