plugins {
	id("approach.android.feature.ui")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.serieslist.ui"
}

dependencies {
	implementation(project(":core:charts"))
	implementation(project(":core:common"))

	implementation(libs.kotlinx.datetime)
	implementation(libs.swipe)
	implementation(libs.vico.compose)
}