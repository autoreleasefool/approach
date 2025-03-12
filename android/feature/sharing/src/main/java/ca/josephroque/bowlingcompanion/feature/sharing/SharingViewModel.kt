package ca.josephroque.bowlingcompanion.feature.sharing

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.filesystem.SystemFileManager
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.GamesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.SeriesRepository
import ca.josephroque.bowlingcompanion.core.model.ShareableGame
import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingAppearance
import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingData
import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingSource
import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingUiAction
import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingUiState
import ca.josephroque.bowlingcompanion.feature.sharing.ui.games.GamesSharingConfigurationUiAction
import ca.josephroque.bowlingcompanion.feature.sharing.ui.games.GamesSharingConfigurationUiState
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
	private val gamesRepository: GamesRepository,
) : ApproachViewModel<SharingScreenEvent>() {

	private val seriesSharingState: MutableStateFlow<SeriesSharingConfigurationUiState> = MutableStateFlow(
		SeriesSharingConfigurationUiState(),
	)

	private var gamesSharingState: MutableStateFlow<GamesSharingConfigurationUiState> = MutableStateFlow(
		GamesSharingConfigurationUiState(),
	)

	private val sharingSource: MutableStateFlow<SharingSource?> = MutableStateFlow(null)

	private val sharingData = combine(
		sharingSource.mapNotNull { it },
		seriesSharingState,
		gamesSharingState,
	) { source, seriesSharingState, gamesSharingState ->
		when (source) {
			is SharingSource.Series ->
				seriesRepository.getShareableSeries(source.seriesId)
					.map { SharingData.Series(it, seriesSharingState) }
			is SharingSource.Game ->
				gamesRepository.getShareableGame(source.gameId)
					.map { SharingData.Games(listOf(it), gamesSharingState) }
			is SharingSource.Statistic -> flowOf(SharingData.Statistic)
			is SharingSource.TeamSeries -> flowOf(SharingData.TeamSeries)

		}
	}
		.flatMapLatest { it }

	private val sharingUiState: Flow<SharingUiState> = combine(
		sharingData,
		seriesSharingState,
		gamesSharingState,
	) { sharingData, seriesSharingState, gamesSharingState ->
		when (sharingData) {
			is SharingData.Series -> SharingUiState.SharingSeries(seriesSharingState, sharingData)
			is SharingData.Games -> SharingUiState.SharingGames(gamesSharingState, sharingData)
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
						is SharingData.Games -> updateDefaultIncludedGames(data.games)
						SharingData.Statistic, SharingData.TeamSeries -> TODO()
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
			is SharingUiAction.GameSharingAction -> handleGamesSharingAction(action.action)
			is SharingUiAction.StatisticSharingAction -> TODO()
		}
	}

	private fun handleSeriesSharingAction(action: SeriesSharingConfigurationUiAction) {
		seriesSharingState.update { it.performAction(action) }
	}

	private fun handleGamesSharingAction(action: GamesSharingConfigurationUiAction) {
		gamesSharingState.update { it.performAction(action) }
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
		gamesSharingState.update { it.copy(appearance = appearance) }
		// TODO: Update StatisticSharingState
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

	private fun updateDefaultIncludedGames(games: List<ShareableGame>) {
		gamesSharingState.update {
			it.copy(
				isGameIncluded = games.map { game ->
					GamesSharingConfigurationUiState.IncludedGame(
						gameId = game.id,
						index = game.index,
						isGameIncluded = it.isGameIncluded
							.firstOrNull { existingGame -> existingGame.gameId == game.id }
							?.isGameIncluded ?: true,
					)
				}
			)
		}
	}
}
