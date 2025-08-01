plugins {
	id("approach.android.library")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.core.statistics.charts"
}

dependencies {
	api(projects.core.charts)
	implementation(projects.core.designsystem)
	implementation(projects.core.model)
	implementation(projects.core.statistics)

	implementation(libs.kotlinx.datetime)
}
