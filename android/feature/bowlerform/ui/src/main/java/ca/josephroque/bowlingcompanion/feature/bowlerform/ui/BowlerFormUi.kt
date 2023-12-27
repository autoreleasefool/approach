package ca.josephroque.bowlingcompanion.feature.bowlerform.ui

import androidx.annotation.StringRes
import ca.josephroque.bowlingcompanion.core.model.BowlerKind

data class BowlerFormUiState(
	val name: String = "",
	@StringRes val nameErrorId: Int? = null,
	val isShowingArchiveDialog: Boolean = false,
	val isArchiveButtonEnabled: Boolean = false,
	val isShowingDiscardChangesDialog: Boolean = false,
)

sealed interface BowlerFormUiAction {
	data object BackClicked: BowlerFormUiAction
	data object DoneClicked: BowlerFormUiAction

	data object DiscardChangesClicked: BowlerFormUiAction
	data object CancelDiscardChangesClicked: BowlerFormUiAction

	data object ArchiveClicked: BowlerFormUiAction
	data object ConfirmArchiveClicked: BowlerFormUiAction
	data object DismissArchiveClicked: BowlerFormUiAction

	data class NameChanged(val name: String): BowlerFormUiAction
}

data class BowlerFormTopBarUiState(
	val existingName: String? = null,
	val kind: BowlerKind = BowlerKind.PLAYABLE,
)