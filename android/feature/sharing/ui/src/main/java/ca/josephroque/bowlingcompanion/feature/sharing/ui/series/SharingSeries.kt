package ca.josephroque.bowlingcompanion.feature.sharing.ui.series

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.SectionHeader
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.Stepper
import ca.josephroque.bowlingcompanion.feature.sharing.ui.R

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SeriesSharing(
	state: SeriesSharingUiState,
	onAction: (SeriesSharingUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier
			.fillMaxSize(),
	) {
		Column(
			modifier = Modifier
				.verticalScroll(rememberScrollState()),
		) {
			SectionHeader(title = stringResource(R.string.sharing_series_modifier_section_overlay))

			FlowRow(
				horizontalArrangement = Arrangement.spacedBy(8.dp),
				modifier = Modifier.padding(horizontal = 16.dp),
			) {
				FilterItem(
					selected = state.isDateChecked,
					title = R.string.sharing_series_modifier_date,
					onClick = { onAction(SeriesSharingUiAction.IsDateCheckedToggled(!state.isDateChecked)) },
					imageVector = Icons.Default.DateRange,
				)

				FilterItem(
					selected = state.isSummaryChecked,
					title = R.string.sharing_series_modifier_summary,
					onClick = { onAction(SeriesSharingUiAction.IsSummaryCheckedToggled(!state.isSummaryChecked)) },
					imageVector = Icons.AutoMirrored.Default.List,
				)

				FilterItem(
					selected = state.isBowlerChecked,
					title = R.string.sharing_series_modifier_bowler,
					onClick = { onAction(SeriesSharingUiAction.IsBowlerCheckedToggled(!state.isBowlerChecked)) },
					imageVector = Icons.Default.Person,
				)

				FilterItem(
					selected = state.isLeagueChecked,
					title = R.string.sharing_series_modifier_league,
					onClick = { onAction(SeriesSharingUiAction.IsLeagueCheckedToggled(!state.isLeagueChecked)) },
					imageVector = Icons.Default.Refresh,
				)
			}

			SectionHeader(title = stringResource(R.string.sharing_series_modifier_section_scores))

			FlowRow(
				horizontalArrangement = Arrangement.spacedBy(8.dp),
				modifier = Modifier.padding(horizontal = 16.dp),
			) {
				FilterItem(
					selected = state.isHighScoreChecked,
					title = R.string.sharing_series_modifier_high_score,
					onClick = {
						onAction(SeriesSharingUiAction.IsHighScoreCheckedToggled(!state.isHighScoreChecked))
					},
					imageVector = Icons.Default.KeyboardArrowUp,
				)

				FilterItem(
					selected = state.isLowScoreChecked,
					title = R.string.sharing_series_modifier_low_score,
					onClick = {
						onAction(SeriesSharingUiAction.IsLowScoreCheckedToggled(!state.isLowScoreChecked))
					},
					imageVector = Icons.Default.KeyboardArrowDown,
				)
			}

			HorizontalDivider(modifier = Modifier.padding(top = 8.dp))

			SectionHeader(title = stringResource(R.string.sharing_series_chart_range))

			Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
				Stepper(
					title = "Lowest Score",
					value = state.chartRange.first,
					range = state.chartLowerBoundRange,
					onValueChanged = { onAction(SeriesSharingUiAction.ChartRangeMinimumChanged(it)) },
					step = 5,
					modifier = Modifier.padding(bottom = 8.dp),
				)

				Stepper(
					title = "Highest Score",
					value = state.chartRange.last,
					range = state.chartUpperBoundRange,
					onValueChanged = { onAction(SeriesSharingUiAction.ChartRangeMaximumChanged(it)) },
					step = 5,
					modifier = Modifier.padding(bottom = 8.dp),
				)
			}

			HorizontalDivider(modifier = Modifier.padding(top = 8.dp))

			SectionHeader(title = stringResource(R.string.sharing_appearance))

			SingleChoiceSegmentedButtonRow(
				modifier = Modifier.padding(horizontal = 16.dp),
			) {
				SharingAppearance.entries.forEachIndexed { index, appearance ->
					SegmentedButton(
						selected = state.appearance == appearance,
						onClick = { onAction(SeriesSharingUiAction.AppearanceChanged(appearance)) },
						shape = SegmentedButtonDefaults.itemShape(
							index = index,
							count = SharingAppearance.entries.size,
						),
					) {
						Text(
							text = appearance.name,
							style = MaterialTheme.typography.bodyMedium,
						)
					}
				}
			}
		}

		HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

		Button(
			onClick = {},
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp),
		) {
			Text("Share")
		}
	}
}

@Composable
private fun FilterItem(
	@StringRes title: Int,
	selected: Boolean,
	onClick: () -> Unit,
	imageVector: ImageVector,
) {
	FilterChip(
		selected = selected,
		onClick = onClick,
		label = {
			Text(
				text = stringResource(title),
				style = MaterialTheme.typography.bodyMedium,
				fontWeight = FontWeight.SemiBold,
			)
		},
		trailingIcon = {
			Icon(
				imageVector,
				contentDescription = null,
				modifier = Modifier.size(18.dp),
			)
		},
	)
}

@Preview
@Composable
private fun SeriesSharingPreview() {
	Surface {
		SeriesSharing(
			state = SeriesSharingUiState(
				appearance = SharingAppearance.Light,
			),
			onAction = {},
		)
	}
}
