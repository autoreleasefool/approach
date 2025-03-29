plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.achievementslist"
}

dependencies {
	implementation(projects.core.achievements)
	implementation(projects.feature.achievementslist.ui)
}
