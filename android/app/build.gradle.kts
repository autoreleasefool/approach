plugins {
	id("com.android.application")
	id("org.jetbrains.kotlin.android")
	kotlin("kapt")
	id("com.google.dagger.hilt.android")
	id("com.google.devtools.ksp")
	id("com.google.protobuf")
	id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

secrets {
	propertiesFileName = "secrets.properties"
}

android {
	namespace = "ca.josephroque.bowlingcompanion"
	compileSdk = 34

	buildFeatures {
		buildConfig = true
	}

	defaultConfig {
		applicationId = "ca.josephroque.bowlingcompanion"
		minSdk = 26
		targetSdk = 34
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
		kotlinCompilerExtensionVersion = "1.5.3"
	}
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}
	ksp {
		arg("room.generateKotlin", "true")
	}
}

ksp {
	arg(RoomSchemaArgProvider(File(projectDir, "schemas")))
}

dependencies {
	implementation("androidx.activity:activity-compose:1.8.0")
	implementation("androidx.core:core-ktx:1.12.0")
	implementation("androidx.compose.ui:ui:1.5.4")
	implementation("androidx.compose.ui:ui-graphics:1.5.4")
	implementation("androidx.compose.ui:ui-tooling-preview:1.5.4")
	implementation("androidx.compose.material3:material3:1.1.2")
	implementation("androidx.datastore:datastore:1.0.0")
	implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
	implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
	implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
	implementation("androidx.navigation:navigation-compose:2.7.4")
	implementation(platform("androidx.compose:compose-bom:2023.09.01"))
	implementation("androidx.room:room-runtime:2.6.0")
	implementation("androidx.room:room-ktx:2.6.0")
	implementation("com.github.TelemetryDeck:KotlinSDK:1.1.0")
	implementation("com.google.dagger:hilt-android:2.48")
	implementation("com.google.protobuf:protobuf-kotlin-lite:3.24.0")
	implementation("com.patrykandpatrick.vico:compose:1.12.0")
	implementation("com.patrykandpatrick.vico:compose-m3:1.12.0")
	implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
	ksp("androidx.room:room-compiler:2.6.0")
	kapt("com.google.dagger:hilt-android-compiler:2.48")

	debugImplementation("androidx.compose.ui:ui-tooling:1.5.4")
	debugImplementation("androidx.compose.ui:ui-test-manifest:1.5.4")

	testImplementation("junit:junit:4.13.2")

	androidTestImplementation("androidx.test.ext:junit:1.1.5")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
	androidTestImplementation(platform("androidx.compose:compose-bom:2023.09.01"))
	androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}

kapt {
	correctErrorTypes = true
}

protobuf {
	protoc {
		artifact = "com.google.protobuf:protoc:3.24.0"
	}
	generateProtoTasks {
		all().forEach { task ->
			task.builtins {
				register("java") {
					option("lite")
				}
				register("kotlin") {
					option("lite")
				}
			}
		}
	}
}

class RoomSchemaArgProvider(
	@get:InputDirectory
	@get:PathSensitive(PathSensitivity.RELATIVE)
	val schemaDir: File
) : CommandLineArgumentProvider {
	override fun asArguments(): Iterable<String> {
		return listOf("room.schemaLocation=${schemaDir.path}")
	}
}