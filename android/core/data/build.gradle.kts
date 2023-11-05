plugins {
	id("approach.android.library")
	id("approach.android.hilt")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.core.data"
}

dependencies {
	implementation(project(":core:common"))
	implementation(project(":core:database"))
	implementation(project(":core:datastore"))
	implementation(project(":core:model"))
	implementation(project(":core:scoring"))

	implementation(libs.androidx.core.ktx)
	implementation(libs.kotlinx.datetime)
}