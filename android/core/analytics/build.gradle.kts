plugins {
	id("approach.android.library")
	id("approach.android.hilt")
	id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.core.analytics"
	buildFeatures {
		buildConfig = true
	}
}

secrets {
	propertiesFileName = "secrets.properties"
}

dependencies {
	implementation(project(":core:model"))

	implementation(libs.telemetrydeck.kotlinsdk)
}