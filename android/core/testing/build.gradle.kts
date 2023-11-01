plugins {
	id("approach.android.library")
	id("approach.android.library.compose")
	id("approach.android.hilt")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.core.testing"
}

dependencies {
	api(libs.junit4)
}