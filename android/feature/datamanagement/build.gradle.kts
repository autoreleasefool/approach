plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.datamanagement"
}

dependencies {
	implementation(project(":core:error"))
	implementation(project(":feature:datamanagement:ui"))

	implementation(libs.kotlinx.datetime)
}
