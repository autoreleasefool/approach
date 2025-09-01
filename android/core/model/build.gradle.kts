plugins {
	id("approach.android.library")
	id("org.jetbrains.kotlin.plugin.parcelize")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.core.model"
}

dependencies {
	api(libs.kotlinx.datetime)
}
