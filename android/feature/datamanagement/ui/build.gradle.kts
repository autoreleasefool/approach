plugins {
	id("approach.android.feature.ui")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.datamanagement.ui"
}

dependencies {
	implementation(libs.androidx.activity.compose)
	implementation(libs.kotlinx.datetime)
}