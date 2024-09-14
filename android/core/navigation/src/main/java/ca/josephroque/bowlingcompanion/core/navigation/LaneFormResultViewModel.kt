package ca.josephroque.bowlingcompanion.core.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import ca.josephroque.bowlingcompanion.core.model.LaneID
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

private const val LANE_FORM_RESULT_KEY = "LaneFormResultKey"

@HiltViewModel
class LaneFormResultViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle) :
	ViewModel() {

	fun getLanes() = savedStateHandle
		.getStateFlow<String?>(LANE_FORM_RESULT_KEY, null)
		.filterNotNull()
		.onEach { savedStateHandle.set<String?>(LANE_FORM_RESULT_KEY, null) }
		.map { it.split(",").map { id -> LaneID.fromString(id) } }

	fun setResult(result: List<LaneID>) {
		savedStateHandle[LANE_FORM_RESULT_KEY] = result.joinToString(",")
	}
}
