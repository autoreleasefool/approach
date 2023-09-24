package ca.josephroque.bowlingcompanion.feature.bowlerslist

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.core.model.BowlerListItem
import ca.josephroque.bowlingcompanion.utils.formatAsAverage
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BowlerItemRow(
	bowler: BowlerListItem,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Card(
		onClick = onClick,
		colors = CardDefaults.cardColors(
			containerColor = colorResource(R.color.purple_100)
		),
		modifier = modifier,
	) {
		Row(
			modifier = modifier
				.fillMaxWidth()
				.padding(vertical = 8.dp, horizontal = 8.dp),
			verticalAlignment = Alignment.CenterVertically,
		) {
			Icon(Icons.Filled.Person, contentDescription = null)
			Spacer(Modifier.width(16.dp))
			Text(
				text = bowler.name,
				fontSize = 18.sp,
				fontWeight = FontWeight.Bold,
			)
			Spacer(
				Modifier
					.defaultMinSize(minWidth = 8.dp)
					.weight(1f)
			)
			Text(bowler.average.formatAsAverage())
		}
	}
}

@Preview
@Composable
fun BowlerCardPreview() {
	Surface {
		BowlerItemRow(
			bowler = BowlerListItem(id = UUID.randomUUID(), name = "Joseph", average = 120.0),
			onClick = {},
		)
	}
}