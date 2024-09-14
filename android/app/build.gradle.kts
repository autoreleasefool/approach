import ca.josephroque.bowlingcompanion.ApproachBuildType

plugins {
	id("approach.android.application")
	id("approach.android.application.compose")
	id("approach.android.room")
	id("approach.android.hilt")
	id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
	alias(libs.plugins.gradle.versions)
	alias(libs.plugins.ktlint)
}

android {
	namespace = "ca.josephroque.bowlingcompanion"

	buildFeatures {
		buildConfig = true
	}

	defaultConfig {
		applicationId = "ca.josephroque.bowlingcompanion"
		versionCode = 328
		versionName = "4.2.0"

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
	implementation(projects.core.analytics)
	implementation(projects.core.common)
	implementation(projects.core.data)
	implementation(projects.core.designsystem)
	implementation(projects.core.error)
	implementation(projects.core.model)
	implementation(projects.core.navigation)
	implementation(projects.core.statistics)
	implementation(projects.feature.accessoriesoverview)
	implementation(projects.feature.alleyform)
	implementation(projects.feature.alleyslist)
	implementation(projects.feature.archives)
	implementation(projects.feature.avatarform)
	implementation(projects.feature.bowlerdetails)
	implementation(projects.feature.bowlerform)
	implementation(projects.feature.datamanagement)
	implementation(projects.feature.featureflagslist)
	implementation(projects.feature.gameseditor)
	implementation(projects.feature.gearlist)
	implementation(projects.feature.gearform)
	implementation(projects.feature.laneform)
	implementation(projects.feature.leaguedetails)
	implementation(projects.feature.leagueform)
	implementation(projects.feature.matchplayeditor)
	implementation(projects.feature.onboarding)
	implementation(projects.feature.opponentslist)
	implementation(projects.feature.overview)
	implementation(projects.feature.quickplay)
	implementation(projects.feature.resourcepicker)
	// Require UI module to get `ResourcePickerType`
	implementation(projects.feature.resourcepicker.ui)
	implementation(projects.feature.seriesdetails)
	implementation(projects.feature.seriesform)
	implementation(projects.feature.settings)
	implementation(projects.feature.statisticsdetails)
	implementation(projects.feature.statisticsoverview)
	implementation(projects.feature.statisticswidget)
	implementation(projects.feature.teamdetails)
	implementation(projects.feature.teamform)
	implementation(projects.feature.teamseriesdetails)

	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.compose.material3)
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.navigation.compose)
	implementation(libs.accompanist.navigation)

	implementation(libs.androidx.compose.ui.tooling.preview)
	debugImplementation(libs.androidx.compose.ui.tooling.preview)

	implementation(libs.sentry)
	implementation(libs.sentry.compose)

	implementation(kotlin("reflect"))
}

secrets {
	defaultPropertiesFileName = "default.secrets.properties"
	propertiesFileName = "secrets.properties"
}

// FIXME: Move to Gradle Convention Plugin
kapt {
	correctErrorTypes = true
}

ktlint {
	android.set(true)
	outputColorName.set("RED")
}
