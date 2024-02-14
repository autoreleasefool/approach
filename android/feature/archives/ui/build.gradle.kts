plugins {
	id("approach.android.feature.ui")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.archives.ui"
}

dependencies {
	implementation(libs.kotlinx.datetime)
	implementation(libs.swipe)
}
