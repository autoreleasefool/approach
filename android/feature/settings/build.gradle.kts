plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.settings"
	buildFeatures {
		buildConfig = true
	}
}

dependencies {
	implementation(project(":core:featureflags"))
	implementation(project(":feature:settings:ui"))
}
