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
	implementation(libs.kotlinx.datetime)
}
