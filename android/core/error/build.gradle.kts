
plugins {
	id("approach.android.library")
	id("approach.android.hilt")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.core.error"
}

dependencies {
	implementation(project(":core:common"))

	implementation(libs.sentry)
}