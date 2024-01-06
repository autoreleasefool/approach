plugins {
	id("approach.android.feature.ui")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.statisticsdetails.ui"
}

dependencies {
	implementation(project(":core:statistics"))
	implementation(project(":core:statistics:charts"))

	implementation(libs.flexiblebottomsheet.m3)
	implementation(libs.vico.compose)
}