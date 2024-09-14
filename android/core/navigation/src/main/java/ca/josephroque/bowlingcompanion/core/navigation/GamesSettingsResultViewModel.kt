package ca.josephroque.bowlingcompanion.core.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

private const val GAMES_SETTINGS_RESULT_KEY = "GamesSettingsResultKey"

@HiltViewModel
class GamesSettingsResultViewModel @Inject constructor(
	private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

	fun getResult() = savedStateHandle
		.getStateFlow<String?>(GAMES_SETTINGS_RESULT_KEY, null)
		.filterNotNull()
		.onEach { savedStateHandle.set<String?>(GAMES_SETTINGS_RESULT_KEY, null) }
		.map {
			val (seriesIds, gameId) = it.split(":")
			Pair(seriesIds.split(",").map { id -> SeriesID.fromString(id) }, GameID.fromString(gameId))
		}

	fun setResult(result: Pair<List<SeriesID>, GameID>) {
		savedStateHandle[GAMES_SETTINGS_RESULT_KEY] = "${result.first.joinToString(",")}:${result.second}"
	}
}
