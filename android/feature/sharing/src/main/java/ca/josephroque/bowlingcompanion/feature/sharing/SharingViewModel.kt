package ca.josephroque.bowlingcompanion.feature.sharing

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.filesystem.SystemFileManager
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.SeriesRepository
import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingAppearance
import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingData
import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingSource
import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingUiAction
import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingUiState
import ca.josephroque.bowlingcompanion.feature.sharing.ui.series.SeriesSharingConfigurationUiAction
import ca.josephroque.bowlingcompanion.feature.sharing.ui.series.SeriesSharingConfigurationUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SharingViewModel @Inject constructor(
// TODO: Add Analytics to Sharing
// 	private val analyticsClient: AnalyticsClient,
	private val fileManager: SystemFileManager,
	private val seriesRepository: SeriesRepository,
) : ApproachViewModel<SharingScreenEvent>() {

	private val seriesSharingState: MutableStateFlow<SeriesSharingConfigurationUiState> = MutableStateFlow(
		SeriesSharingConfigurationUiState(),
	)

	private val sharingSource: MutableStateFlow<SharingSource?> = MutableStateFlow(null)

	private val sharingData = combine(
		sharingSource.mapNotNull { it },
		seriesSharingState,
	) { source, seriesSharingState ->
		when (source) {
			is SharingSource.Series ->
				seriesRepository.getShareableSeries(source.seriesId)
					.map { SharingData.Series(it, seriesSharingState) }
			is SharingSource.Game -> flowOf(SharingData.Game)
			is SharingSource.Statistic -> flowOf(SharingData.Statistic)
			is SharingSource.TeamSeries -> flowOf(SharingData.TeamSeries)

		}
	}
		.flatMapLatest { it }

	private val sharingUiState: Flow<SharingUiState> = combine(
		sharingData,
		seriesSharingState,
	) { sharingData, seriesSharingState ->
		when (sharingData) {
			is SharingData.Series -> SharingUiState.SharingSeries(seriesSharingState, sharingData)
			is SharingData.Game -> SharingUiState.SharingGame
			is SharingData.Statistic -> SharingUiState.SharingStatistic
			is SharingData.TeamSeries -> SharingUiState.SharingTeamSeries
		}
	}

	val uiState: StateFlow<SharingScreenUiState> = sharingUiState
		.map { SharingScreenUiState.Sharing(it) }
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = SharingScreenUiState.Loading,
		)

	init {
		viewModelScope.launch {
			sharingData
				.collectLatest { data ->
					when (data) {
						is SharingData.Series -> updateDefaultChartRanges(data.series.scores)
						SharingData.Game, SharingData.Statistic, SharingData.TeamSeries -> TODO()
					}
				}
		}
	}

	fun handleAction(action: SharingScreenUiAction) {
		when (action) {
			is SharingScreenUiAction.Sharing -> handleSharingAction(action.action)
			is SharingScreenUiAction.DidStartSharing -> {
				loadSource(action.source)
				setDefaultAppearance(isSystemInDarkTheme = action.isSystemInDarkTheme)
			}
		}
	}

	private fun handleSharingAction(action: SharingUiAction) {
		when (action) {
			is SharingUiAction.ShareButtonClicked -> shareImage(action.image)
			is SharingUiAction.SeriesSharingAction -> handleSeriesSharingAction(action.action)
			is SharingUiAction.GameSharingAction -> TODO()
			is SharingUiAction.StatisticSharingAction -> TODO()
		}
	}

	private fun handleSeriesSharingAction(action: SeriesSharingConfigurationUiAction) {
		when (action) {
			is SeriesSharingConfigurationUiAction.IsDateCheckedToggled ->
				toggleIsDateChecked(isDateChecked = action.isDateChecked)
			is SeriesSharingConfigurationUiAction.IsSeriesTotalCheckedToggled ->
				toggleIsSeriesTotalChecked(isSeriesTotalChecked = action.isSeriesTotalChecked)
			is SeriesSharingConfigurationUiAction.IsBowlerCheckedToggled ->
				toggleIsBowlerChecked(isBowlerChecked = action.isBowlerChecked)
			is SeriesSharingConfigurationUiAction.IsLeagueCheckedToggled ->
				toggleIsLeagueChecked(isLeagueChecked = action.isLeagueChecked)
			is SeriesSharingConfigurationUiAction.IsHighScoreCheckedToggled ->
				toggleIsHighScoreChecked(isHighScoreChecked = action.isHighScoreChecked)
			is SeriesSharingConfigurationUiAction.IsLowScoreCheckedToggled ->
				toggleIsLowScoreChecked(isLowScoreChecked = action.isLowScoreChecked)
			is SeriesSharingConfigurationUiAction.ChartRangeMinimumChanged ->
				updateChartRangeMinimum(minimum = action.minimum)
			is SeriesSharingConfigurationUiAction.ChartRangeMaximumChanged ->
				updateChartRangeMaximum(maximum = action.maximum)
			is SeriesSharingConfigurationUiAction.AppearanceChanged ->
				updateAppearance(appearance = action.appearance)
		}
	}

	private fun shareImage(image: Deferred<ImageBitmap>) {
		viewModelScope.launch {
			try {
				val bitmap = image.await()
				fileManager.sharedImagesDir.mkdirs()

				val file = File(fileManager.sharedImagesDir, "shared_image.png")
				val stream = file.outputStream()
				bitmap.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream)
				stream.flush()
				stream.close()

				sendEvent(SharingScreenEvent.LaunchShareIntent(file))
			} catch (e: Throwable) {
				// TODO: Handle error thrown capturing image
			}
		}
	}

	private fun loadSource(source: SharingSource) {
		sharingSource.value = source
	}

	private fun setDefaultAppearance(isSystemInDarkTheme: Boolean) {
		val appearance = if (isSystemInDarkTheme) SharingAppearance.Dark else SharingAppearance.Light
		seriesSharingState.update { it.copy(appearance = appearance) }
		// TODO: Update GameSharingState and StatisticSharingState
	}

	private fun toggleIsDateChecked(isDateChecked: Boolean) {
		seriesSharingState.update { it.copy(isDateChecked = isDateChecked) }
	}

	private fun toggleIsSeriesTotalChecked(isSeriesTotalChecked: Boolean) {
		seriesSharingState.update { it.copy(isSeriesTotalChecked = isSeriesTotalChecked) }
	}

	private fun toggleIsBowlerChecked(isBowlerChecked: Boolean) {
		seriesSharingState.update { it.copy(isBowlerChecked = isBowlerChecked) }
	}

	private fun toggleIsLeagueChecked(isLeagueChecked: Boolean) {
		seriesSharingState.update { it.copy(isLeagueChecked = isLeagueChecked) }
	}

	private fun toggleIsHighScoreChecked(isHighScoreChecked: Boolean) {
		seriesSharingState.update { it.copy(isHighScoreChecked = isHighScoreChecked) }
	}

	private fun toggleIsLowScoreChecked(isLowScoreChecked: Boolean) {
		seriesSharingState.update { it.copy(isLowScoreChecked = isLowScoreChecked) }
	}

	private fun updateDefaultChartRanges(scores: List<Int>) {
		val rangeMinimum = max((scores.minOrNull() ?: 0) - 5, 0)
		val rangeMaximum = min((scores.maxOrNull() ?: 0) + 5, 450)

		seriesSharingState.update {
			it.copy(
				chartLowerBoundRange = IntRange(0, rangeMinimum),
				chartUpperBoundRange = IntRange(rangeMaximum, 450),
				chartRange = if (it.chartRange.isEmpty()) IntRange(rangeMinimum, rangeMaximum) else it.chartRange,
			)
		}
	}

	private fun updateChartRangeMinimum(minimum: Int) {
		seriesSharingState.update {
			it.copy(chartRange = IntRange(start = minimum, endInclusive = it.chartRange.last))
		}
	}

	private fun updateChartRangeMaximum(maximum: Int) {
		seriesSharingState.update {
			it.copy(chartRange = IntRange(start = it.chartRange.first, endInclusive = maximum))
		}
	}

	private fun updateAppearance(appearance: SharingAppearance) {
		seriesSharingState.update { it.copy(appearance = appearance) }
	}
}
