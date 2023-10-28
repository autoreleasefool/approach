plugins {
	id("approach.android.library")
	id("approach.android.hilt")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.core.featureflags"
	buildFeatures {
		buildConfig = true
	}
}

dependencies {
}