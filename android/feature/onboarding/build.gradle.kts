plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.onboarding"
}

dependencies {
	implementation(project(":core:database"))
	implementation(project(":feature:onboarding:ui"))

	implementation(libs.kotlinx.datetime)
}
