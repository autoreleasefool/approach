package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.rolleditor

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ca.josephroque.bowlingcompanion.core.model.GearListItem
import java.util.UUID

@Composable
fun RollEditor(
	state: RollEditorUiState,
	onSelectBall: (UUID) -> Unit,
	onToggleFoul: (Boolean) -> Unit,
	modifier: Modifier = Modifier,
) {
}

data class RollEditorUiState(
	val recentBalls: List<GearListItem> = emptyList(),
	val selectedBall: UUID? = null,
	val didFoulRoll: Boolean = false,
)