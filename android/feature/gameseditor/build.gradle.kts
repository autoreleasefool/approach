plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.gameseditor"
}

dependencies {
	implementation(projects.core.featureflags)
	implementation(projects.core.scoresheet)
	implementation(projects.core.statistics)
	implementation(projects.feature.gameseditor.ui)
	implementation(projects.feature.sharing)
	implementation(projects.feature.sharing.ui)
}
