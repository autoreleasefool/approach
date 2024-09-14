package ca.josephroque.bowlingcompanion.core.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

private const val SCORE_EDITOR_RESULT_KEY = "ScoreEditorResultKey"

@HiltViewModel
class ScoreEditorResultViewModel @Inject constructor(
	private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

	fun getScore() = savedStateHandle
		.getStateFlow<String?>(SCORE_EDITOR_RESULT_KEY, null)
		.filterNotNull()
		.onEach { savedStateHandle.set<String?>(SCORE_EDITOR_RESULT_KEY, null) }
		.map {
			val (method, score) = it.split(":")
			GameScoringMethod.valueOf(method) to score.toInt()
		}

	fun setResult(result: Pair<GameScoringMethod, Int>) {
		savedStateHandle[SCORE_EDITOR_RESULT_KEY] = "${result.first}:${result.second}"
	}
}
