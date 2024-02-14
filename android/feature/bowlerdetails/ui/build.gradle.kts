plugins {
	id("approach.android.feature.ui")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.bowlerdetails.ui"
}

dependencies {
	implementation(project(":feature:gearlist:ui"))
	implementation(project(":feature:leagueslist:ui"))
	implementation(project(":feature:statisticswidget:ui"))
}
