plugins {
	id("approach.android.feature.ui")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.avatarform.ui"
}

dependencies {
	implementation(libs.colorpicker.compose)
}
