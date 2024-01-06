plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.statisticsdetails"
}

dependencies {
	implementation(project(":core:statistics"))
	implementation(project(":core:statistics:charts"))
	implementation(project(":feature:statisticsdetails:ui"))

	implementation(libs.flexiblebottomsheet.m3)
	implementation(libs.vico.compose)
}