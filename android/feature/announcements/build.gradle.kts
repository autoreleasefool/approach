plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.announcements"
}

dependencies {
	implementation(projects.feature.announcements.ui)
}
