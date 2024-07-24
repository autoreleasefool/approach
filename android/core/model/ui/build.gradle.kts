plugins {
	id("approach.android.library")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.core.model.ui"
}

dependencies {
	implementation(projects.core.common)
	implementation(projects.core.designsystem)
	implementation(projects.core.model)
	implementation(libs.kotlinx.datetime)
}
