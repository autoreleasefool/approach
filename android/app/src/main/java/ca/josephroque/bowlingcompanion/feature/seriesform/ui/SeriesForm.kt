package ca.josephroque.bowlingcompanion.feature.seriesform.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.core.components.form.FormSection
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import kotlinx.datetime.LocalDate

@Composable
internal fun SeriesForm(
	date: LocalDate,
	onDateChanged: (LocalDate) -> Unit,
	preBowl: SeriesPreBowl,
	onPreBowlChanged: (SeriesPreBowl) -> Unit,
	excludeFromStatistics: ExcludeFromStatistics,
	onExcludeFromStatisticsChanged: (ExcludeFromStatistics) -> Unit,
	modifier: Modifier = Modifier,
	numberOfGames: Int? = null,
	onNumberOfGamesChanged: ((Int) -> Unit)? = null,
) {
	Column(
		modifier = modifier
			.verticalScroll(rememberScrollState())
			.fillMaxSize(),
	) {
		numberOfGames?.let {
			FormSection(
				titleResourceId = R.string.league_form_property_number_of_games,
				modifier = Modifier.padding(vertical = 16.dp)
			) {
				NumberOfGamesSlider(it, onNumberOfGamesChanged)
			}
		}


	}
}

@Composable
private fun NumberOfGamesSlider(numberOfGames: Int, onNumberOfGamesChanged: ((Int) -> Unit)?) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp),
	) {
		OutlinedTextField(
			value = numberOfGames.toString(),
			onValueChange = {
				val intValue = it.toIntOrNull() ?: 1
				onNumberOfGamesChanged?.invoke(intValue)
			},
			label = { Text(stringResource(R.string.league_form_property_number_of_games)) },
			singleLine = true,
			modifier = Modifier.weight(1f),
		)

		Row(
			verticalAlignment = Alignment.CenterVertically,
		) {
			IconButton(onClick = { onNumberOfGamesChanged?.invoke(numberOfGames - 1) }) {
				Icon(
					painter = painterResource(R.drawable.ic_minus_circle),
					contentDescription = stringResource(R.string.cd_decrement),
					tint = MaterialTheme.colorScheme.onSurface,
				)
			}

			IconButton(onClick = { onNumberOfGamesChanged?.invoke(numberOfGames + 1) }) {
				Icon(
					painter = painterResource(R.drawable.ic_add_circle),
					contentDescription = stringResource(R.string.cd_increment),
					tint = MaterialTheme.colorScheme.onSurface,
				)
			}
		}
	}
}

@Preview
@Composable
private fun SeriesFormPreview() {
	Surface {
		SeriesForm(
			numberOfGames = 4,
			onNumberOfGamesChanged = {},
			date = LocalDate.parse("2023-10-01"),
			onDateChanged = {},
			preBowl = SeriesPreBowl.REGULAR,
			onPreBowlChanged = {},
			excludeFromStatistics = ExcludeFromStatistics.INCLUDE,
			onExcludeFromStatisticsChanged = {},
		)
	}
}