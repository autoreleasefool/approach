plugins {
	id("approach.android.feature.ui")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.settings.ui"
}

dependencies {
	implementation(projects.core.common)
}
