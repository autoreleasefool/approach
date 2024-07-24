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
	implementation(projects.core.featureflags)
	implementation(projects.feature.settings.ui)
}
