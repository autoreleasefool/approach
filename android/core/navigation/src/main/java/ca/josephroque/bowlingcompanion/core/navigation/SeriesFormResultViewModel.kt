package ca.josephroque.bowlingcompanion.core.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

const val SERIES_FORM_RESULT_KEY = "SeriesFormResultKey"

@HiltViewModel
class SeriesFormResultViewModel @Inject constructor(
	private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

	fun getSeriesID() = savedStateHandle
		.getStateFlow<String?>(SERIES_FORM_RESULT_KEY, null)
		.filterNotNull()
		.onEach { savedStateHandle.set<String?>(SERIES_FORM_RESULT_KEY, null) }
		.map { SeriesID.fromString(it) }

	fun setResult(result: SeriesID) {
		savedStateHandle[SERIES_FORM_RESULT_KEY] = result
	}
}
