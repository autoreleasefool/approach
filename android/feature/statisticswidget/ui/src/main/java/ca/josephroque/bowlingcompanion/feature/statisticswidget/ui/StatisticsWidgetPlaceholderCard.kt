package ca.josephroque.bowlingcompanion.feature.statisticswidget.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import ca.josephroque.bowlingcompanion.core.designsystem.R as RCoreDesign
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsWidgetPlaceholderCard(
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Card(
		onClick = onClick,
		colors = CardDefaults.cardColors(
			containerColor = colorResource(RCoreDesign.color.purple_300),
			contentColor = colorResource(RCoreDesign.color.white),
		),
		modifier = modifier.fillMaxWidth(),
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
		) {
			Image(
				painterResource(R.drawable.analytics),
				contentDescription = null,
				modifier = Modifier
					.size(80.dp)
					.align(Alignment.BottomEnd)
					.alpha(0.3F)
			)
			Column(
				verticalArrangement = Arrangement.spacedBy(4.dp),
			) {
				Text(
					text = stringResource(R.string.statistics_widget_placeholder_title),
					style = MaterialTheme.typography.headlineMedium,
				)
				Text(
					text = stringResource(R.string.statistics_widget_placeholder_message),
					style = MaterialTheme.typography.titleMedium,
				)
			}
		}
	}
}

@Preview
@Composable
private fun StatisticsWidgetPlaceholderPreview() {
	Surface {
		StatisticsWidgetPlaceholderCard(onClick = {}, modifier = Modifier.padding(16.dp))
	}
}