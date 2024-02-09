package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.R
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.components.NavigationButton

@Composable
internal fun Header(
	state: GameDetailsUiState.HeaderUiState,
	onAction: (GameDetailsUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier,
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(16.dp),
			modifier = Modifier.fillMaxWidth(),
		) {
			BowlerDetails(
				name = state.bowlerName,
				league = state.leagueName,
			)

			NextButton(
				next = state.nextElement,
				onClick = {
					if (state.nextElement != null)
						onAction(GameDetailsUiAction.NextGameElementClicked(state.nextElement))
				}
			)
		}

		if (state.hasMultipleBowlers) {
			ViewAllBowlersButton(
				onClick = { onAction(GameDetailsUiAction.ViewAllBowlersClicked) },
				modifier = Modifier.padding(top = 16.dp),
			)
		}
	}
}

@Composable
private fun RowScope.BowlerDetails(
	name: String,
	league: String,
	modifier: Modifier = Modifier,
) {
	Column(
		horizontalAlignment = Alignment.Start,
		modifier = modifier.weight(1f),
	) {
		Text(
			text = name,
			style = MaterialTheme.typography.titleMedium,
			fontWeight = FontWeight.Black,
		)
		Text(
			text = league,
			style = MaterialTheme.typography.bodyMedium
		)
	}
}

@Composable
private fun ViewAllBowlersButton(
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	NavigationButton(
		title = stringResource(R.string.game_editor_view_all_bowlers),
		onClick = onClick,
		icon = {
			Icon(
				painterResource(R.drawable.ic_number_list),
				contentDescription = null,
			)
		},
		modifier = modifier,
	)
}

@Composable
private fun NextButton(
	next: NextGameEditableElement?,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Box(
		modifier = modifier.alpha(if (next == null) 0f else 1f)
	) {
		Surface(
			modifier = Modifier
				.heightIn(min = 44.dp),
			shape = MaterialTheme.shapes.medium,
			color = MaterialTheme.colorScheme.primaryContainer,
			onClick = onClick,
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(2.dp),
			) {
				Column(
					horizontalAlignment = Alignment.End,
					modifier = Modifier.padding(start = 8.dp),
				) {
					Text(
						text = when (next) {
							is NextGameEditableElement.Roll -> stringResource(R.string.game_editor_next_roll)
							is NextGameEditableElement.Frame -> stringResource(R.string.game_editor_next_frame)
							is NextGameEditableElement.Game -> stringResource(R.string.game_editor_next_game)
							null -> ""
						},
						style = MaterialTheme.typography.bodySmall,
						fontWeight = FontWeight.Bold,
					)

					Text(
						text = when (next) {
							is NextGameEditableElement.Roll -> stringResource(
								R.string.game_editor_ball_ordinal,
								next.rollIndex + 1
							)

							is NextGameEditableElement.Frame -> stringResource(
								R.string.game_editor_frame_ordinal,
								next.frameIndex + 1
							)

							is NextGameEditableElement.Game -> stringResource(
								R.string.game_editor_game_ordinal,
								next.gameIndex + 1
							)

							null -> ""
						},
						style = MaterialTheme.typography.labelSmall,
						color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
					)
				}

				Icon(
					painter = painterResource(ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_chevron_right),
					contentDescription = null,
					tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
				)
			}
		}
	}
}

@Preview
@Composable
private fun HeaderPreview() {
	Surface {
		Header(
			state = GameDetailsUiState.HeaderUiState(
				bowlerName = "Joseph",
				leagueName = "Majors 22/23",
				nextElement = NextGameEditableElement.Frame(1),
				hasMultipleBowlers = true,
			),
			onAction = {},
			modifier = Modifier.padding(16.dp),
		)
	}
}