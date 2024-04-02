plugins {
	id("approach.android.library")
	id("approach.android.hilt")
	alias(libs.plugins.protobuf)
}

android {
	namespace = "ca.josephroque.bowlingcompanion.core.datastore"
}

dependencies {
	implementation(project(":core:common"))
	implementation(project(":core:error"))
	implementation(project(":core:model"))

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
