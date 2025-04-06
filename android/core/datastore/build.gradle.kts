plugins {
	id("approach.android.library")
	id("approach.android.hilt")
	alias(libs.plugins.protobuf)
}

android {
	defaultConfig {
		consumerProguardFiles("consumer-proguard-rules.pro")
	}

	namespace = "ca.josephroque.bowlingcompanion.core.datastore"
}

dependencies {
	implementation(projects.core.common)
	implementation(projects.core.error)
	implementation(projects.core.model)

	implementation(libs.androidx.dataStore.core)
	implementation(libs.protobuf.kotlin.lite)
}

protobuf {
	protoc {
		artifact = libs.protobuf.protoc.get().toString()
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
