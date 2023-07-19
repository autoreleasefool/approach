package ca.josephroque.bowlingcompanion.feature.bowlerslist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.model.Bowler
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import java.util.UUID

@Composable
fun BowlerItemRow(
	bowler: Bowler,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		modifier = modifier
			.fillMaxWidth()
			.padding(vertical = 8.dp)
			.clickable(onClick = onClick),
		horizontalArrangement = Arrangement.SpaceBetween,
	) {
		Text(bowler.name)
		Text("213")
	}
}

@Preview
@Composable
fun BowlerCardPreview() {
	Surface {
		BowlerItemRow(
			bowler = Bowler(id = UUID.randomUUID(), name = "Joseph", kind = BowlerKind.PLAYABLE),
			onClick = { },
		)
	}
}