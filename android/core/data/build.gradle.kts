plugins {
	id("approach.android.library")
	id("approach.android.hilt")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.core.data"
}

dependencies {
	implementation(projects.core.achievements)
	implementation(projects.core.common)
	implementation(projects.core.database)
	implementation(projects.core.datastore)
	implementation(projects.core.model)
	implementation(projects.core.scoring)
	implementation(projects.core.statistics)

	implementation(libs.androidx.core.ktx)
	implementation(libs.kotlinx.datetime)
	implementation(libs.paging)
}
