plugins {
	id("approach.android.feature.ui")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.statisticsdetails.ui"
}

dependencies {
	implementation(project(":core:common"))
	implementation(project(":core:charts"))
	implementation(project(":core:statistics"))
	implementation(project(":core:statistics:charts"))

	implementation(libs.kotlinx.datetime)
	implementation(libs.vico.compose)
}
