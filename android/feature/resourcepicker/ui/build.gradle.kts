plugins {
	id("approach.android.feature.ui")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.resourcepicker.ui"
}

dependencies {
	implementation(project(":core:common"))

	implementation(libs.kotlinx.datetime)
}