package ca.josephroque.bowlingcompanion.feature.gearform.ui

import androidx.annotation.StringRes
import ca.josephroque.bowlingcompanion.core.model.Avatar
import ca.josephroque.bowlingcompanion.core.model.BowlerDetails
import ca.josephroque.bowlingcompanion.core.model.GearKind

data class GearFormUiState(
	val name: String,
	val kind: GearKind,
	@StringRes val nameErrorId: Int?,
	val owner: BowlerDetails?,
	val avatar: Avatar,
	val isShowingDeleteDialog: Boolean,
	val isDeleteButtonEnabled: Boolean,
)

sealed interface GearFormUiAction {
	data object BackClicked: GearFormUiAction
	data object DoneClicked: GearFormUiAction

	data object DeleteClicked: GearFormUiAction
	data object ConfirmDeleteClicked: GearFormUiAction
	data object DismissDeleteClicked: GearFormUiAction

	data object AvatarClicked: GearFormUiAction
	data object OwnerClicked: GearFormUiAction

	data class NameChanged(val name: String): GearFormUiAction
}

data class GearFormTopBarUiState(
	val existingName: String?,
)