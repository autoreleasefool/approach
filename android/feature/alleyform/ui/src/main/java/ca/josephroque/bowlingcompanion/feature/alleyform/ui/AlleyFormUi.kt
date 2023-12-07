package ca.josephroque.bowlingcompanion.feature.alleyform.ui

import androidx.annotation.StringRes
import ca.josephroque.bowlingcompanion.core.model.AlleyMaterial
import ca.josephroque.bowlingcompanion.core.model.AlleyMechanism
import ca.josephroque.bowlingcompanion.core.model.AlleyPinBase
import ca.josephroque.bowlingcompanion.core.model.AlleyPinFall
import ca.josephroque.bowlingcompanion.core.model.LaneListItem

data class AlleyFormUiState(
	val name: String,
	@StringRes val nameErrorId: Int?,
	val material: AlleyMaterial?,
	val pinFall: AlleyPinFall?,
	val mechanism: AlleyMechanism?,
	val pinBase: AlleyPinBase?,
	val lanes: List<LaneListItem>,

	val isShowingDeleteDialog: Boolean,
	val isDeleteButtonEnabled: Boolean,
)

sealed interface AlleyFormUiAction {
	data object BackClicked: AlleyFormUiAction
	data object DoneClicked: AlleyFormUiAction

	data object DeleteClicked: AlleyFormUiAction
	data object ConfirmDeleteClicked: AlleyFormUiAction
	data object DismissDeleteClicked: AlleyFormUiAction

	data object ManageLanesClicked: AlleyFormUiAction

	data class NameChanged(val name: String): AlleyFormUiAction
	data class MaterialChanged(val material: AlleyMaterial?): AlleyFormUiAction
	data class PinFallChanged(val pinFall: AlleyPinFall?): AlleyFormUiAction
	data class MechanismChanged(val mechanism: AlleyMechanism?): AlleyFormUiAction
	data class PinBaseChanged(val pinBase: AlleyPinBase?): AlleyFormUiAction
}

data class AlleyFormTopBarUiState(
	val existingName: String? = null,
)