plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.avatarform"
}

dependencies {
	implementation(projects.core.model.ui)
	implementation(projects.feature.avatarform.ui)
}
