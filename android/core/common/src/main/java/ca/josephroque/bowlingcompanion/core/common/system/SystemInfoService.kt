package ca.josephroque.bowlingcompanion.core.common.system

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

interface SystemInfoService {
	val versionName: String
	val versionCode: String

	@OptIn(ExperimentalTime::class)
	val firstInstallTime: Instant
}
