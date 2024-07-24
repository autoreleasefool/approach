plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.archives"
}

dependencies {
	implementation(projects.feature.archives.ui)

	implementation(libs.kotlinx.datetime)
}
