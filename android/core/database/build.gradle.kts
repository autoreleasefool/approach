plugins {
	id("approach.android.library")
	id("approach.android.hilt")
	id("approach.android.room")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.core.database"
}

dependencies {
	implementation(project(":core:model"))

	implementation(libs.kotlinx.datetime)
}