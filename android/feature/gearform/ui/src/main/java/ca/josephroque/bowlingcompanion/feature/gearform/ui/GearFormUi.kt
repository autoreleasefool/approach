package ca.josephroque.bowlingcompanion.feature.gearform.ui

import androidx.annotation.StringRes
import ca.josephroque.bowlingcompanion.core.model.Avatar
import ca.josephroque.bowlingcompanion.core.model.BowlerDetails
import ca.josephroque.bowlingcompanion.core.model.GearKind

data class GearFormUiState(
	val name: String = "",
	val kind: GearKind = GearKind.BOWLING_BALL,
	@StringRes val nameErrorId: Int? = null,
	val owner: BowlerDetails? = null,
	val avatar: Avatar = Avatar.default(),
	val isAvatarLabelOverridden: Boolean = false,
	val isShowingDeleteDialog: Boolean = false,
	val isDeleteButtonEnabled: Boolean = false,
	val isShowingDiscardChangesDialog: Boolean = false,
)

sealed interface GearFormUiAction {
	data object BackClicked: GearFormUiAction
	data object DoneClicked: GearFormUiAction

	data object DeleteClicked: GearFormUiAction
	data object ConfirmDeleteClicked: GearFormUiAction
	data object DismissDeleteClicked: GearFormUiAction

	data object DiscardChangesClicked: GearFormUiAction
	data object CancelDiscardChangesClicked: GearFormUiAction

	data object AvatarClicked: GearFormUiAction
	data object OwnerClicked: GearFormUiAction

	data class NameChanged(val name: String): GearFormUiAction
	data class KindChanged(val kind: GearKind): GearFormUiAction
}

data class GearFormTopBarUiState(
	val existingName: String? = null,
)