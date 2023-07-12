package ca.josephroque.bowlingcompanion.feature.bowlerslist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.data.models.Bowler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BowlerCard(
	bowler: Bowler,
	onClick: () -> Unit,
) {
	Card(
		onClick = onClick,
		shape = RoundedCornerShape(16.dp),
	) {
		Box(modifier = Modifier.padding(16.dp)) {
			Text(text = bowler.name)
		}
	}
}