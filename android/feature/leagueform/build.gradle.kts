plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.leagueform"
}

dependencies {
	implementation(project(":feature:leagueform:ui"))
	implementation(libs.kotlinx.datetime)
}
