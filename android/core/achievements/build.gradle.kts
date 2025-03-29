plugins {
	id("approach.android.library")
	id("approach.android.hilt")
	id("org.jetbrains.kotlin.plugin.parcelize")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.core.achievements"
}

dependencies {
	implementation(projects.core.common)
	implementation(projects.core.model)

	implementation(libs.kotlinx.datetime)
}
