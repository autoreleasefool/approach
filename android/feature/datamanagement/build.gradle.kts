plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.datamanagement"
}

dependencies {
	implementation(projects.core.error)
	implementation(projects.feature.datamanagement.ui)

	implementation(libs.kotlinx.datetime)
}
