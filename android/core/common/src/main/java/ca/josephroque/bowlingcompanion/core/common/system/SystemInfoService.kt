package ca.josephroque.bowlingcompanion.core.common.system

import kotlinx.datetime.Instant

interface SystemInfoService {
	val versionName: String
	val versionCode: String

	val firstInstallTime: Instant
}
