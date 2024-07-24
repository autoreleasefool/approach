plugins {
	id("approach.android.library")
	id("approach.android.hilt")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.core.scoring"
}

dependencies {
	implementation(projects.core.common)
	implementation(projects.core.model)
}
