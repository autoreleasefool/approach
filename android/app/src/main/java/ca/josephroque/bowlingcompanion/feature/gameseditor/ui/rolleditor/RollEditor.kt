package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.rolleditor

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ca.josephroque.bowlingcompanion.core.model.GearListItem
import java.util.UUID

@Composable
fun RollEditor(
	rollEditorState: RollEditorUiState,
	onSelectBall: (UUID) -> Unit,
	onToggleFoul: (Boolean) -> Unit,
	modifier: Modifier = Modifier,
) {
	when (rollEditorState) {
		RollEditorUiState.Loading -> Unit
		is RollEditorUiState.Edit -> RollEditor(
			state = rollEditorState,
			onSelectBall = onSelectBall,
			onToggleFoul = onToggleFoul,
			modifier = modifier,
		)
	}
}

@Composable
private fun RollEditor(
	state: RollEditorUiState.Edit,
	onSelectBall: (UUID) -> Unit,
	onToggleFoul: (Boolean) -> Unit,
	modifier: Modifier = Modifier,
) {

}

sealed interface RollEditorUiState {
	data object Loading: RollEditorUiState
	data class Edit(
		val recentBalls: List<GearListItem>,
		val selectedBall: UUID,
		val didFoulRoll: Boolean,
	): RollEditorUiState
}