plugins {
	id("approach.android.feature.ui")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.sharing.ui"
}

dependencies {
	implementation(projects.core.charts)
	implementation(projects.core.common)
	implementation(projects.core.model.charts.ui)

	implementation(libs.kotlinx.datetime)
	implementation(libs.vico.compose)
}
