plugins {
	id("approach.android.library")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.core.statistics.charts"
}

dependencies {
	implementation(project(":core:charts"))
	implementation(project(":core:designsystem"))
	implementation(project(":core:model"))
	implementation(project(":core:statistics"))

	implementation(libs.kotlinx.datetime)
	implementation(libs.vico.compose)
}