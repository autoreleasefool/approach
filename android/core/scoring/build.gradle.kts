plugins {
	id("approach.android.library")
	id("approach.android.hilt")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.core.scoring"
}

dependencies {
	implementation(project(":core:model"))
}