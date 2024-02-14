plugins {
	id("approach.android.library")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.core.charts"
}

dependencies {
	implementation(project(":core:designsystem"))

	implementation(libs.vico.compose)
}
