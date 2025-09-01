package ca.josephroque.bowlingcompanion.core.common.system

import kotlin.time.Instant

interface SystemInfoService {
	val versionName: String
	val versionCode: String

	val firstInstallTime: Instant
}
