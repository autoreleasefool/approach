import ca.josephroque.bowlingcompanion.ApproachBuildType

plugins {
	id("approach.android.application")
	id("approach.android.application.compose")
	id("approach.android.room")
	id("approach.android.hilt")
}

android {
	namespace = "ca.josephroque.bowlingcompanion"

	buildFeatures {
		buildConfig = true
	}

	defaultConfig {
		applicationId = "ca.josephroque.bowlingcompanion"
		versionCode = 321
		versionName = "4.0.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		vectorDrawables {
			useSupportLibrary = true
		}
	}

	buildTypes {
		debug {
			applicationIdSuffix = ApproachBuildType.DEBUG.applicationIdSuffix
		}
		release {
			isMinifyEnabled = false
			applicationIdSuffix = ApproachBuildType.RELEASE.applicationIdSuffix
			proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}
}

dependencies {
	implementation(project(":core:analytics"))
	implementation(project(":core:common"))
	implementation(project(":core:data"))
	implementation(project(":core:database"))
	implementation(project(":core:datastore"))
	implementation(project(":core:designsystem"))
	implementation(project(":core:featureflags"))
	implementation(project(":core:model"))
	implementation(project(":core:model:ui"))
	implementation(project(":feature:alleyslist"))
	implementation(project(":feature:alleyslist:ui"))
	implementation(project(":feature:analytics"))
	implementation(project(":feature:analytics:ui"))
	implementation(project(":feature:gameseditor"))
	implementation(project(":feature:gameseditor:ui"))
	implementation(project(":feature:statisticswidget"))

	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.compose.foundation)
	implementation(libs.androidx.compose.foundation.layout)
	implementation(libs.androidx.compose.material3)
	implementation(libs.androidx.compose.ui.tooling.preview)
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.hilt.navigation.compose)
	implementation(libs.androidx.lifecycle.runtimeCompose)
	implementation(libs.androidx.navigation.compose)

	implementation(libs.kotlinx.datetime)
	implementation(libs.vico.compose)
	implementation(libs.vico.compose.m3)

	debugImplementation(libs.androidx.compose.ui.tooling.preview)
}

// TODO: Move to Gradle Convention Plugin
kapt {
	correctErrorTypes = true
}
