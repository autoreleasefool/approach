plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.resourcepicker"
}

dependencies {
	implementation(project(":core:model:ui"))
	implementation(project(":feature:resourcepicker:ui"))

	implementation(libs.kotlinx.datetime)
}
