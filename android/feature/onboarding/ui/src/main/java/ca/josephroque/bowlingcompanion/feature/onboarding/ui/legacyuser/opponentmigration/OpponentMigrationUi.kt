package ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.opponentmigration

import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.core.model.OpponentListItem
import java.util.UUID

data class MergedBowler(
	val id: UUID,
	val name: String,
	val mergedBowlerNames: List<String>,
	val kind: BowlerKind,
)

data class OpponentMigrationUiState(
	val isMigrating: Boolean = false,
	val list: List<MergedBowler>,
	val selected: Set<UUID>,
	val dialog: OpponentMigrationDialogUiState? = null,
)

sealed interface OpponentMigrationUiAction {
	data class OpponentClicked(val opponent: MergedBowler) : OpponentMigrationUiAction
	data class OpponentNameDialog(
		val action: OpponentMigrationNameDialogUiAction,
	) : OpponentMigrationUiAction
	data class TooManyBowlersDialog(
		val action: TooManyBowlersDialogUiAction,
	) : OpponentMigrationUiAction
}

sealed interface OpponentMigrationDialogUiState {
	data class NameDialog(val name: String) : OpponentMigrationDialogUiState
	data class TooManyBowlersDialog(
		val firstName: String,
		val secondName: String,
	) : OpponentMigrationDialogUiState
}

sealed interface TooManyBowlersDialogUiAction {
	data object Dismissed : TooManyBowlersDialogUiAction
}

sealed interface OpponentMigrationNameDialogUiAction {
	data object Dismissed : OpponentMigrationNameDialogUiAction
	data object Confirmed : OpponentMigrationNameDialogUiAction
	data class NameChanged(val name: String) : OpponentMigrationNameDialogUiAction
}

sealed interface OpponentMigrationTopBarUiAction {
	data object DoneClicked : OpponentMigrationTopBarUiAction
}

sealed interface OpponentMigrationBottomBarUiState {
	data object StartMigration : OpponentMigrationBottomBarUiState
	data class Migrating(
		val isMergeEnabled: Boolean = false,
		val isUndoEnabled: Boolean = false,
	) : OpponentMigrationBottomBarUiState
}

sealed interface OpponentMigrationBottomBarUiAction {
	data object StartMigrationClicked : OpponentMigrationBottomBarUiAction
	data object MergeClicked : OpponentMigrationBottomBarUiAction
	data object UndoClicked : OpponentMigrationBottomBarUiAction
}
