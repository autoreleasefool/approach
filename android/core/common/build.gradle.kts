plugins {
	id("approach.android.library")
	id("approach.android.hilt")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.core.common"
}

dependencies {
	implementation(projects.core.error)

	implementation(libs.androidx.navigation.compose)
	api(libs.kotlinx.datetime)
}
