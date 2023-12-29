package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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

@Composable
internal fun Header(
	state: GameDetailsUiState.HeaderUiState,
	onAction: (GameDetailsUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(16.dp),
		modifier = modifier.fillMaxWidth(),
	) {
		Column(
			horizontalAlignment = Alignment.Start,
			modifier = Modifier.weight(1f),
		) {
			Text(
				text = state.bowlerName,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Black,
			)
			Text(
				text = state.leagueName,
				style = MaterialTheme.typography.bodyMedium
			)
		}

		Box(
			modifier = Modifier.alpha(if (state.nextElement == null) 0f else 1f)
		) {
			Surface(
				modifier = Modifier
					.heightIn(min = 44.dp),
				shape = MaterialTheme.shapes.medium,
				color = MaterialTheme.colorScheme.primaryContainer,
				onClick = {
					state.nextElement ?: return@Surface
					onAction(GameDetailsUiAction.NextGameElementClicked(state.nextElement))
				},
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
							text = when (state.nextElement) {
								is NextGameEditableElement.Roll -> stringResource(R.string.game_editor_next_roll)
								is NextGameEditableElement.Frame -> stringResource(R.string.game_editor_next_frame)
								is NextGameEditableElement.Game -> stringResource(R.string.game_editor_next_game)
								null -> ""
							},
							style = MaterialTheme.typography.bodySmall,
							fontWeight = FontWeight.Bold,
						)

						Text(
							text = when (state.nextElement) {
								is NextGameEditableElement.Roll -> stringResource(
									R.string.game_editor_ball_ordinal,
									state.nextElement.rollIndex + 1
								)

								is NextGameEditableElement.Frame -> stringResource(
									R.string.game_editor_frame_ordinal,
									state.nextElement.frameIndex + 1
								)

								is NextGameEditableElement.Game -> stringResource(
									R.string.game_editor_game_ordinal,
									state.nextElement.gameIndex + 1
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
}



@Preview
@Composable
private fun HeaderPreview() {
	Surface {
		Header(
			state = GameDetailsUiState.HeaderUiState(
				bowlerName = "Joseph",
				leagueName = "Majors 22/23",
				nextElement = NextGameEditableElement.Frame(1)
			),
			onAction = {},
			modifier = Modifier.padding(16.dp),
		)
	}
}