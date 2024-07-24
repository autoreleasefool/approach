
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
	defaultPropertiesFileName = "default.secrets.properties"
	propertiesFileName = "secrets.properties"
}

dependencies {
	implementation(projects.core.common)
	implementation(projects.core.data)
	implementation(projects.core.model)

	implementation(libs.telemetrydeck.kotlinsdk)
}
