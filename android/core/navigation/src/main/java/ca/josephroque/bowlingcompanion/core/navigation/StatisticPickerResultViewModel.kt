package ca.josephroque.bowlingcompanion.core.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

private const val STATISTIC_PICKER_RESULT_KEY = "StatisticPickerResultKey"

@HiltViewModel
class StatisticPickerResultViewModel @Inject constructor(
	private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

	fun getStatisticID() = savedStateHandle
		.getStateFlow<String?>(STATISTIC_PICKER_RESULT_KEY, null)
		.filterNotNull()
		.onEach { savedStateHandle.remove<String?>(STATISTIC_PICKER_RESULT_KEY) }
		.map { StatisticID.valueOf(it) }

	fun setResult(result: StatisticID) {
		savedStateHandle[STATISTIC_PICKER_RESULT_KEY] = result
	}
}
