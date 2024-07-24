plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.onboarding"
}

dependencies {
	implementation(projects.core.database)
	implementation(projects.feature.onboarding.ui)

	implementation(libs.kotlinx.datetime)
}
