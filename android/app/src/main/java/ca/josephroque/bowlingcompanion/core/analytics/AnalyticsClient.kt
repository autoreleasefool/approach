package ca.josephroque.bowlingcompanion.core.analytics

import kotlinx.coroutines.flow.Flow

interface AnalyticsClient {
	fun initialize()

	suspend fun trackEvent(event: TrackableEvent)
	suspend fun setGlobalProperty(key: String, value: String?)

	val optInStatus: Flow<AnalyticsOptInStatus>
	suspend fun setOptInStatus(status: AnalyticsOptInStatus)
}

enum class AnalyticsOptInStatus {
	OPTED_IN,
	OPTED_OUT,
}