plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.resourcepicker"
}

dependencies {
	implementation(projects.core.model.ui)
	implementation(projects.feature.resourcepicker.ui)

	implementation(libs.kotlinx.datetime)
}
