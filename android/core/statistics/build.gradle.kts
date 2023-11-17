plugins {
	id("approach.android.library")
	id("approach.android.hilt")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.core.statistics"
}

dependencies {
	implementation(project(":core:model"))

	implementation(libs.kotlinx.datetime)
}