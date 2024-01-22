@file:Suppress("SpellCheckingInspection", "SpellCheckingInspection")

import ca.josephroque.bowlingcompanion.ApproachBuildType

plugins {
	id("approach.android.application")
	id("approach.android.application.compose")
	id("approach.android.room")
	id("approach.android.hilt")
	id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
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

		manifestPlaceholders["sentryAppDsn"] = ""
		manifestPlaceholders["telemetryDeckAppId"] = ""
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
	implementation(project(":core:designsystem"))
	implementation(project(":core:error"))
	implementation(project(":core:model"))
	implementation(project(":core:navigation"))
	implementation(project(":core:statistics"))
	implementation(project(":feature:accessoriesoverview"))
	implementation(project(":feature:alleyform"))
	implementation(project(":feature:alleyslist"))
	implementation(project(":feature:archives"))
	implementation(project(":feature:avatarform"))
	implementation(project(":feature:bowlerdetails"))
	implementation(project(":feature:bowlerform"))
	implementation(project(":feature:datamanagement"))
	implementation(project(":feature:gameseditor"))
	implementation(project(":feature:gearlist"))
	implementation(project(":feature:gearform"))
	implementation(project(":feature:laneform"))
	implementation(project(":feature:leaguedetails"))
	implementation(project(":feature:leagueform"))
	implementation(project(":feature:matchplayeditor"))
	implementation(project(":feature:onboarding"))
	implementation(project(":feature:opponentslist"))
	implementation(project(":feature:overview"))
	implementation(project(":feature:resourcepicker"))
	implementation(project(":feature:resourcepicker:ui")) // Require UI module to get `ResourcePickerType`
	implementation(project(":feature:seriesdetails"))
	implementation(project(":feature:seriesform"))
	implementation(project(":feature:settings"))
	implementation(project(":feature:statisticsdetails"))
	implementation(project(":feature:statisticsoverview"))
	implementation(project(":feature:statisticswidget"))

	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.compose.material3)
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.navigation.compose)

	implementation(libs.androidx.compose.ui.tooling.preview)
	debugImplementation(libs.androidx.compose.ui.tooling.preview)

	implementation(kotlin("reflect"))
}

secrets {
	propertiesFileName = "secrets.properties"
}

// FIXME: Move to Gradle Convention Plugin
kapt {
	correctErrorTypes = true
}
