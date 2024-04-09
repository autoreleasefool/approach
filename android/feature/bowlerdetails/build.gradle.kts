plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.bowlerdetails"
}

dependencies {
	implementation(project(":core:statistics"))
	implementation(project(":core:statistics:charts"))
	implementation(project(":feature:bowlerdetails:ui"))
	implementation(project(":feature:leagueslist:ui"))
	implementation(project(":feature:gearlist:ui"))
	implementation(project(":feature:statisticswidget:ui"))

	implementation(libs.kotlinx.datetime)
	implementation(libs.vico.compose)
}
