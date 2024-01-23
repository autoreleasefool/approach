plugins {
	id("approach.android.library")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.core.designsystem"
}

dependencies {
	implementation(project(":core:common"))

	api(libs.androidx.compose.foundation)
	api(libs.androidx.compose.foundation.layout)
	api(libs.androidx.compose.material3)
	api(libs.androidx.compose.runtime)
	api(libs.androidx.compose.ui.tooling.preview)
	api(libs.androidx.core.ktx)

	debugApi(libs.androidx.compose.ui.tooling)
}