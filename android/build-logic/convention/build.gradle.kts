import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	`kotlin-dsl`
}

group = "ca.josephroque.bowlingcompanion.buildlogic"

// Configure the build-logic plugins to target JDK 17
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}
kotlin {
	compilerOptions {
		jvmTarget = JvmTarget.JVM_17
	}
}

dependencies {
	compileOnly(libs.android.gradlePlugin)
	compileOnly(libs.compose.gradlePlugin)
	compileOnly(libs.kotlin.gradlePlugin)
	compileOnly(libs.ksp.gradlePlugin)
}

gradlePlugin {
	plugins {
		register("androidApplicationCompose") {
			id = "approach.android.application.compose"
			implementationClass = "AndroidApplicationComposeConventionPlugin"
		}
		register("androidApplication") {
			id = "approach.android.application"
			implementationClass = "AndroidApplicationConventionPlugin"
		}
		register("androidLibraryCompose") {
			id = "approach.android.library.compose"
			implementationClass = "AndroidLibraryComposeConventionPlugin"
		}
		register("androidLibrary") {
			id = "approach.android.library"
			implementationClass = "AndroidLibraryConventionPlugin"
		}
		register("androidFeature") {
			id = "approach.android.feature"
			implementationClass = "AndroidFeatureConventionPlugin"
		}
		register("androidFeatureUi") {
			id = "approach.android.feature.ui"
			implementationClass = "AndroidFeatureUiConventionPlugin"
		}
		register("androidTest") {
			id = "approach.android.test"
			implementationClass = "AndroidTestConventionPlugin"
		}
		register("androidHilt") {
			id = "approach.android.hilt"
			implementationClass = "AndroidHiltConventionPlugin"
		}
		register("androidRoom") {
			id = "approach.android.room"
			implementationClass = "AndroidRoomConventionPlugin"
		}
		register("jvmLibrary") {
			id = "approach.jvm.library"
			implementationClass = "JvmLibraryConventionPlugin"
		}
	}
}
