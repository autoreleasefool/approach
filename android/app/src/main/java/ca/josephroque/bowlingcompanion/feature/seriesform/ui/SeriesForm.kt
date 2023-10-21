package ca.josephroque.bowlingcompanion.feature.seriesform.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.core.components.form.FormSection
import ca.josephroque.bowlingcompanion.core.components.form.Stepper
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
			FormSection(titleResourceId = R.string.league_form_property_number_of_games) {
				NumberOfGamesSlider(it, onNumberOfGamesChanged)
			}
		}


	}
}

@Composable
private fun NumberOfGamesSlider(numberOfGames: Int, onNumberOfGamesChanged: ((Int) -> Unit)?) {
	Stepper(
		title = stringResource(R.string.league_form_property_number_of_games),
		value = numberOfGames,
		onValueChanged = onNumberOfGamesChanged ?: {},
	)
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