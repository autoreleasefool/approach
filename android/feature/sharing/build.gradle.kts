plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.sharing"
	buildFeatures {
		buildConfig = true
	}
}

dependencies {
	implementation(projects.feature.sharing.ui)
}
