plugins {
	id("approach.android.library")
	id("approach.android.hilt")
	id("approach.android.room")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.core.database"
}

dependencies {
	implementation(projects.core.model)
	implementation(projects.core.statistics)

	implementation(libs.kotlinx.datetime)
	implementation(libs.paging)
	implementation(libs.room.paging)
}
