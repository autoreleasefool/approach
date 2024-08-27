plugins {
	id("approach.android.library")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.core.model.charts.ui"
}

dependencies {
	implementation(projects.core.charts)
	implementation(projects.core.common)
	implementation(projects.core.designsystem)
	implementation(projects.core.model)
	implementation(projects.core.model.ui)

	implementation(libs.kotlinx.datetime)
	implementation(libs.vico.compose)
}
