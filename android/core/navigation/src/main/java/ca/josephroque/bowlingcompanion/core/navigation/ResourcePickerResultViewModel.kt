package ca.josephroque.bowlingcompanion.core.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

@HiltViewModel
class ResourcePickerResultViewModel @Inject constructor(
	private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

	fun <T> getSelectedIds(key: String, parse: (UUID) -> T) = savedStateHandle
		.getStateFlow<Set<UUID>?>(key, null)
		.filterNotNull()
		.map { it.map { id -> parse(id) }.toSet() }

	fun setSelectedIds(key: String, result: Set<UUID>) {
		savedStateHandle[key] = result
	}
}
