plugins {
	id("approach.android.feature.ui")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.sharing.ui"
}

dependencies {
	implementation(projects.core.common)
}
