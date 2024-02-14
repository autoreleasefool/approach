plugins {
	id("approach.android.library")
	id("approach.android.hilt")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.core.testing"
}

dependencies {
	api(libs.junit4)
	api(libs.kotlinx.coroutines.test)
}
