package ca.josephroque.bowlingcompanion.core.navigation

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.parcelize.Parcelize

@JvmInline
@Parcelize
value class ResourcePickerResultKey(val value: String) : Parcelable {
	override fun toString(): String = value
}

@HiltViewModel
class ResourcePickerResultViewModel @Inject constructor(
	private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

	fun <T> getSelectedIds(key: ResourcePickerResultKey, parse: (UUID) -> T) = savedStateHandle
		.getStateFlow<Set<UUID>?>(key.value, null)
		.filterNotNull()
		.map {
			savedStateHandle.set<Set<UUID>>(key.value, null)
			it.map { id -> parse(id) }.toSet()
		}

	fun setSelectedIds(key: ResourcePickerResultKey, result: Set<UUID>) {
		savedStateHandle[key.value] = result
	}
}
