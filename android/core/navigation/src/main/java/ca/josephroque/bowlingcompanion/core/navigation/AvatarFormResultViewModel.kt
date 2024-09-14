package ca.josephroque.bowlingcompanion.core.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import ca.josephroque.bowlingcompanion.core.model.Avatar
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

private const val AVATAR_FORM_RESULT_KEY = "AvatarFormResultKey"

@HiltViewModel
class AvatarFormResultViewModel @Inject constructor(
	private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

	fun getAvatar() = savedStateHandle
		.getStateFlow<String?>(AVATAR_FORM_RESULT_KEY, null)
		.filterNotNull()
		.onEach { savedStateHandle.remove<String?>(AVATAR_FORM_RESULT_KEY) }
		.map { Avatar.fromString(it) }

	fun setResult(result: Avatar) {
		savedStateHandle[AVATAR_FORM_RESULT_KEY] = result.toString()
	}
}
