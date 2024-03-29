plugins {
	id("approach.android.library")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.core.model.ui"
}

dependencies {
	implementation(project(":core:common"))
	implementation(project(":core:designsystem"))
	implementation(project(":core:model"))
	implementation(libs.kotlinx.datetime)
}
