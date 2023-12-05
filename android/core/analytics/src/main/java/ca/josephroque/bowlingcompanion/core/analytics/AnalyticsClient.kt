package ca.josephroque.bowlingcompanion.core.analytics

import ca.josephroque.bowlingcompanion.core.model.AnalyticsOptInStatus
import kotlinx.coroutines.flow.Flow

interface AnalyticsClient {
	suspend fun initialize()

	suspend fun trackEvent(event: TrackableEvent)
	suspend fun setGlobalProperty(key: String, value: String?)

	val optInStatus: Flow<AnalyticsOptInStatus>
	suspend fun setOptInStatus(status: AnalyticsOptInStatus)
}