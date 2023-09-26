package ca.josephroque.bowlingcompanion.feature.statisticswidget.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.josephroque.bowlingcompanion.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StatisticsWidgetPlaceholderCard(
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	OutlinedCard(
		onClick = onClick,
		colors = CardDefaults.cardColors(
			containerColor = colorResource(R.color.purple_300),
			contentColor = colorResource(R.color.white),
		),
		elevation = CardDefaults.cardElevation(),
		modifier = modifier.fillMaxWidth(),
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
		) {
			Image(
				painterResource(R.drawable.ic_analytics),
				contentDescription = null,
				modifier = Modifier
					.size(80.dp)
					.align(Alignment.BottomEnd)
					.alpha(0.3F)
			)
			Column {
				Text(
					stringResource(R.string.statistics_placeholder_title),
					fontSize = 24.sp,
				)
				Text(stringResource(R.string.statistics_placeholder_message))
			}
		}
	}
}

@Preview
@Composable
fun StatisticsWidgetPlaceholderPreview() {
	Surface {
		StatisticsWidgetPlaceholderCard(onClick = {}, modifier = Modifier.padding(16.dp))
	}
}