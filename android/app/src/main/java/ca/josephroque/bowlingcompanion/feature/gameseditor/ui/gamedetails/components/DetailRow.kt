package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun DetailRow(
	modifier: Modifier = Modifier,
	content: @Composable RowScope.() -> Unit
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(16.dp),
		modifier = modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp),
	) {
		content()
	}
}