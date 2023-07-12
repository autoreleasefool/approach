plugins {
	id("com.android.application")
	id("org.jetbrains.kotlin.android")
	kotlin("kapt")
	id("com.google.dagger.hilt.android")
}

android {
	namespace = "ca.josephroque.bowlingcompanion"
	compileSdk = 33

	defaultConfig {
		applicationId = "ca.josephroque.bowlingcompanion"
		minSdk = 24
		targetSdk = 33
		versionCode = 321
		versionName = "4.0.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		vectorDrawables {
			useSupportLibrary = true
		}
	}

	buildTypes {
		debug {
			applicationIdSuffix = ".debug"
		}
		release {
			isMinifyEnabled = false
			proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}
	kotlinOptions {
		jvmTarget = "17"
	}
	buildFeatures {
		compose = true
	}
	composeOptions {
		kotlinCompilerExtensionVersion = "1.4.3"
	}
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}
}

dependencies {
	implementation("androidx.activity:activity-compose:1.7.2")
	implementation("androidx.core:core-ktx:1.9.0")
	implementation("androidx.compose.ui:ui")
	implementation("androidx.compose.ui:ui-graphics")
	implementation("androidx.compose.ui:ui-tooling-preview")
	implementation("androidx.compose.material3:material3")
	implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
	implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.1")
	implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
	implementation("androidx.navigation:navigation-compose:2.6.0")
	implementation(platform("androidx.compose:compose-bom:2023.03.00"))
	implementation("com.google.dagger:hilt-android:2.44")
	kapt("com.google.dagger:hilt-android-compiler:2.44")

	debugImplementation("androidx.compose.ui:ui-tooling")
	debugImplementation("androidx.compose.ui:ui-test-manifest")

	testImplementation("junit:junit:4.13.2")

	androidTestImplementation("androidx.test.ext:junit:1.1.5")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
	androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
	androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}

kapt {
	correctErrorTypes = true
}