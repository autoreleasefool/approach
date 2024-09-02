plugins {
	id("approach.android.library")
	id("approach.android.hilt")
	id("org.jetbrains.kotlin.plugin.parcelize")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.core.statistics"
}

dependencies {
	implementation(projects.core.model)

	implementation(libs.kotlinx.datetime)
}
