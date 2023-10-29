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

	implementation(libs.androidx.compose.foundation)
	implementation(libs.androidx.compose.foundation.layout)
	implementation(libs.androidx.compose.material3)
	implementation(libs.androidx.compose.ui.tooling.preview)
	implementation(libs.androidx.core.ktx)
	implementation(libs.kotlinx.datetime)

	implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
	implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
	implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
	implementation("androidx.navigation:navigation-compose:2.7.4")
	implementation(platform("androidx.compose:compose-bom:2023.09.01"))
	implementation("com.google.dagger:hilt-android:2.48")
	implementation("com.patrykandpatrick.vico:compose:1.12.0")
	implementation("com.patrykandpatrick.vico:compose-m3:1.12.0")
	kapt("com.google.dagger:hilt-android-compiler:2.48")

	debugImplementation("androidx.compose.ui:ui-tooling:1.5.4")
	debugImplementation("androidx.compose.ui:ui-test-manifest:1.5.4")
}

// TODO: Move to Gradle Convention Plugin
kapt {
	correctErrorTypes = true
}
