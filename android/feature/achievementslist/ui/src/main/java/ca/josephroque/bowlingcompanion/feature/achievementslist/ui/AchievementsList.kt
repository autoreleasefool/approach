package ca.josephroque.bowlingcompanion.feature.achievementslist.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.achievements.earnable
import ca.josephroque.bowlingcompanion.core.common.utils.toLocalDate
import ca.josephroque.bowlingcompanion.feature.achievementslist.ui.components.AchievementListGridItem

@Composable
fun AchievementsList(
	state: AchievementsListUiState,
	onAction: (AchievementsListUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	LazyVerticalGrid(
		columns = GridCells.Adaptive(minSize = 128.dp),
		modifier = modifier.padding(horizontal = 16.dp),
	) {
		item(
			span = { GridItemSpan(maxLineSpan) },
		) {
			Column(
				verticalArrangement = Arrangement.spacedBy(4.dp),
				modifier = Modifier.padding(bottom = 16.dp),
			) {
				Text(
					text = stringResource(R.string.achievements_list_header_checkBack),
					style = MaterialTheme.typography.bodyMedium,
				)

				Text(
					text = stringResource(R.string.achievements_list_header_soon),
					style = MaterialTheme.typography.bodyMedium,
				)
			}
		}

		items(state.achievements) { achievement ->
			val earnableAchievement = achievement.earnable() ?: return@items

			AchievementListGridItem(
				title = stringResource(earnableAchievement.id.titleResourceId),
				icon = painterResource(earnableAchievement.id.iconResourceId),
				firstEarnedOn = achievement.firstEarnedAt.toLocalDate(),
				earnedCount = achievement.count,
				modifier = Modifier
					.clickable(onClick = { onAction(AchievementsListUiAction.AchievementClicked(achievement)) }),
			)
		}
	}
}
