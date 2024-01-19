package ca.josephroque.bowlingcompanion.core.analytics

import ca.josephroque.bowlingcompanion.core.model.AnalyticsOptInStatus
import kotlinx.coroutines.flow.Flow

interface AnalyticsClient {
	suspend fun initialize()

	fun trackEvent(event: TrackableEvent)
	fun startNewGameSession()
	suspend fun setGlobalProperty(key: String, value: String?)

	val optInStatus: Flow<AnalyticsOptInStatus>
	suspend fun setOptInStatus(status: AnalyticsOptInStatus)
}