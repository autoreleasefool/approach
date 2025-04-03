package ca.josephroque.bowlingcompanion.feature.achievementslist.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.common.utils.compactFormat
import ca.josephroque.bowlingcompanion.core.common.utils.toLocalDate
import ca.josephroque.bowlingcompanion.feature.achievementslist.ui.R
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate

@Composable
fun AchievementListGridItem(
	title: String,
	icon: Painter,
	firstEarnedOn: LocalDate?,
	earnedCount: Int,
	modifier: Modifier = Modifier,
) {
	Card(
		modifier = modifier,
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(8.dp),
			modifier = Modifier.padding(8.dp),
		) {
			Image(
				icon,
				contentDescription = null,
				modifier = Modifier
					.fillMaxWidth()
					.aspectRatio(1f),
			)

			Column(
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.spacedBy(4.dp),
			) {
				Text(
					text = title,
					style = MaterialTheme.typography.titleSmall,
					textAlign = TextAlign.Center,
				)

				Text(
					text = firstEarnedOn?.let {
						stringResource(R.string.achievements_list_item_first_earned, it.compactFormat())
					} ?: stringResource(R.string.achievements_list_item_locked),
					style = MaterialTheme.typography.bodyMedium,
					textAlign = TextAlign.Center,
				)

				if (earnedCount > 0 && firstEarnedOn != null) {
					Text(
						text = pluralStringResource(R.plurals.achievements_list_item_earned_count, earnedCount, earnedCount),
						style = MaterialTheme.typography.bodySmall,
						textAlign = TextAlign.Center,
					)
				}
			}
		}
	}
}

@Preview
@Composable
fun AchievementListGridItemPreview() {
	Surface {
		AchievementListGridItem(
			title = "Ten Years",
			icon = painterResource(ca.josephroque.bowlingcompanion.core.achievements.R.drawable.achievement_ten_years),
			firstEarnedOn = Clock.System.now().toLocalDate(),
			earnedCount = 1,
		)
	}
}
