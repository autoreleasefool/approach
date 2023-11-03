package ca.josephroque.bowlingcompanion.core.analytics

import android.app.Application
import android.content.Context
import android.util.Log
// import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.core.model.AnalyticsOptInStatus
import com.telemetrydeck.sdk.TelemetryManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class TelemetryDeckAnalyticsClient @Inject constructor(
	@ApplicationContext private val context: Context,
	// private val userDataRepository: UserDataRepository,
): AnalyticsClient {
	companion object {
		private const val TAG = "ca.josephroque.bowlingcompanion.core.analytics.TelemetryDeckAnalyticsClient"
	}

//	 override val optInStatus = userDataRepository.userData
//	 	.map { it.analyticsOptIn }
	override val optInStatus: Flow<AnalyticsOptInStatus>
		get() = MutableStateFlow(AnalyticsOptInStatus.OPTED_IN)

	private val globalProperties: MutableStateFlow<Map<String, String>> =
		MutableStateFlow(mapOf())

	override fun initialize() {
		val application = context.applicationContext as? Application
		val appId = BuildConfig.telemetryDeckAppId

		if (appId.isBlank()) {
			Log.d(TAG, "Analytics disabled - AppId unavailable")
			TelemetryManager.stop()
			return
		} else if (application == null) {
			Log.d(TAG, "Analytics disabled - Application unavailable")
			TelemetryManager.stop()
			return
		}

		val builder = TelemetryManager.Builder()
			.appID(appId)
			.showDebugLogs(true)

		TelemetryManager.start(application, builder)
	}

	override suspend fun setGlobalProperty(key: String, value: String?) {
		globalProperties.update {
			it.toMutableMap().apply {
				if (value == null) {
					this.remove(key)
				} else {
					this[key] = value
				}
			}
		}
	}

	override suspend fun trackEvent(event: TrackableEvent) {
		val eventPayload = event.payload ?: mapOf()
		val globalProperties = globalProperties.value
		val additionalPayload = globalProperties + eventPayload

		TelemetryManager.queue(signalType = event.name, additionalPayload = additionalPayload)
	}

	override suspend fun setOptInStatus(status: AnalyticsOptInStatus) {
		// userDataRepository.setAnalyticsOptInStatus(status)
	}
}