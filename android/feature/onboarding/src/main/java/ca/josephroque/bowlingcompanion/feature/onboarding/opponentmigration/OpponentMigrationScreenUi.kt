package ca.josephroque.bowlingcompanion.feature.onboarding.opponentmigration

import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.opponentmigration.OpponentMigrationBottomBarUiAction
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.opponentmigration.OpponentMigrationBottomBarUiState
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.opponentmigration.OpponentMigrationTopBarUiAction
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.opponentmigration.OpponentMigrationUiAction
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.opponentmigration.OpponentMigrationUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

sealed interface OpponentMigrationScreenUiState {
	data object Loading : OpponentMigrationScreenUiState
	data class Loaded(
		val opponentMigration: OpponentMigrationUiState,
		val bottomBar: OpponentMigrationBottomBarUiState,
	) : OpponentMigrationScreenUiState
}

sealed interface OpponentMigrationScreenUiAction {
	data class OpponentMigration(val action: OpponentMigrationUiAction) :
		OpponentMigrationScreenUiAction

	data class TopBar(val action: OpponentMigrationTopBarUiAction) :
		OpponentMigrationScreenUiAction

	data class BottomBar(val action: OpponentMigrationBottomBarUiAction) :
		OpponentMigrationScreenUiAction
}

sealed interface OpponentMigrationScreenEvent {
	data object Dismissed : OpponentMigrationScreenEvent
	data object FinishedMigration : OpponentMigrationScreenEvent
}

fun MutableStateFlow<OpponentMigrationScreenUiState>.updateLoaded(
	function: (OpponentMigrationScreenUiState.Loaded) -> OpponentMigrationScreenUiState.Loaded,
) {
	this.update { state ->
		when (state) {
			OpponentMigrationScreenUiState.Loading -> state
			is OpponentMigrationScreenUiState.Loaded -> function(state)
		}
	}
}
