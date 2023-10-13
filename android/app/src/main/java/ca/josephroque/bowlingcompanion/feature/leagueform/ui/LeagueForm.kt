package ca.josephroque.bowlingcompanion.feature.leagueform.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Divider
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
import ca.josephroque.bowlingcompanion.core.components.form.FormRadioGroup
import ca.josephroque.bowlingcompanion.core.components.form.FormSection
import ca.josephroque.bowlingcompanion.core.components.form.FormSwitch
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import ca.josephroque.bowlingcompanion.feature.leagueform.GamesPerSeries
import ca.josephroque.bowlingcompanion.feature.leagueform.IncludeAdditionalPinFall

@Composable
internal fun LeagueForm(
	name: String,
	nameErrorId: Int?,
	excludeFromStatistics: ExcludeFromStatistics,
	includeAdditionalPinFall: IncludeAdditionalPinFall,
	additionalPinFall: Int,
	additionalGames: Int,
	modifier: Modifier = Modifier,
	recurrence: LeagueRecurrence? = null,
	numberOfGames: Int? = null,
	gamesPerSeries: GamesPerSeries? = null,
	onRecurrenceChanged: ((LeagueRecurrence) -> Unit)? = null,
	onNumberOfGamesChanged: ((Int) -> Unit)? = null,
	onGamesPerSeriesChanged: ((GamesPerSeries) -> Unit)? = null,
	onNameChanged: ((String) -> Unit)? = null,
	onExcludeFromStatisticsChanged: ((ExcludeFromStatistics) -> Unit)? = null,
	onIncludeAdditionalPinFallChanged: ((IncludeAdditionalPinFall) -> Unit)? = null,
	onAdditionalPinFallChanged: ((Int) -> Unit)? = null,
	onAdditionalGamesChanged: ((Int) -> Unit)? = null,
) {
	Column(
		modifier = modifier
			.verticalScroll(rememberScrollState())
			.fillMaxSize(),
	)
	{
		FormSection(
			titleResourceId = R.string.league_form_details_title,
			modifier = Modifier.padding(vertical = 16.dp),
		) {
			LeagueNameField(name, onNameChanged, nameErrorId)
		}

		recurrence?.let {
			Divider()
			FormSection(Modifier.padding(top = 16.dp)) {
				RecurrencePicker(it, onRecurrenceChanged)
			}
		}

		if (recurrence != null || gamesPerSeries != null) {
			Divider()

			when (recurrence) {
				LeagueRecurrence.ONCE, null -> Unit
				LeagueRecurrence.REPEATING -> gamesPerSeries?.let {
					FormSection(Modifier.padding(top = 16.dp)) {
						GamesPerSeriesPicker(it, onGamesPerSeriesChanged)
					}
				}
			}

			when (gamesPerSeries) {
				GamesPerSeries.DYNAMIC, null -> Unit
				GamesPerSeries.STATIC -> numberOfGames?.let {
					FormSection(
						titleResourceId = when (recurrence) {
							LeagueRecurrence.ONCE -> R.string.league_form_property_number_of_games
							LeagueRecurrence.REPEATING, null -> null
						},
						modifier = Modifier.padding(vertical = 16.dp)
					) {
						NumberOfGamesSlider(it, onNumberOfGamesChanged)
					}
				}
			}
		}

		Divider()
		IncludeAdditionalPinFallSwitch(includeAdditionalPinFall, onIncludeAdditionalPinFallChanged)
		when (includeAdditionalPinFall) {
			IncludeAdditionalPinFall.INCLUDE -> {
				Column(
					verticalArrangement = Arrangement.spacedBy(8.dp),
					modifier = Modifier
						.fillMaxWidth()
						.padding(bottom = 16.dp),
				) {
					AdditionalPinFallField(additionalPinFall, onAdditionalPinFallChanged)
					AdditionalGamesField(additionalGames, onAdditionalGamesChanged)
				}
			}
			IncludeAdditionalPinFall.NONE -> Unit
		}

		Divider()
		FormSection(Modifier.padding(top = 16.dp)) {
			ExcludeFromStatisticsPicker(excludeFromStatistics, onExcludeFromStatisticsChanged)
		}
	}
}

@Composable
internal fun LeagueNameField(name: String, onNameChanged: ((String) -> Unit)?, errorId: Int?) {
	OutlinedTextField(
		value = name,
		onValueChange = onNameChanged ?: {},
		label = { Text(stringResource(R.string.league_form_property_name)) },
		singleLine = true,
		isError = errorId != null,
		supportingText = {
			errorId?.let {
				Text(
					text = stringResource(it),
					color = MaterialTheme.colorScheme.error,
					modifier = Modifier.fillMaxWidth(),
				)
			}
		},
		trailingIcon = {
			if (errorId != null) {
				Icon(
					Icons.Default.Warning,
					tint = MaterialTheme.colorScheme.error,
					contentDescription = null
				)
			}
		},
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp)
	)
}

@Composable
internal fun RecurrencePicker(recurrence: LeagueRecurrence, onRecurrenceChanged: ((LeagueRecurrence) -> Unit)?) {
	FormRadioGroup(
		title = stringResource(R.string.league_form_property_repeat),
		subtitle = stringResource(
			R.string.league_form_property_repeat_footer,
			stringResource(R.string.league_form_property_repeat_repeats),
			stringResource(R.string.league_form_property_repeat_never),
		),
		options = LeagueRecurrence.values(),
		selected = recurrence,
		titleForOption = {
			when (it) {
				LeagueRecurrence.REPEATING -> stringResource(R.string.league_form_property_repeat_repeats)
				LeagueRecurrence.ONCE -> stringResource(R.string.league_form_property_repeat_never)
			}
		},
		onOptionSelected = onRecurrenceChanged ?: { }
	)
}

@Composable
internal fun ExcludeFromStatisticsPicker(excludeFromStatistics: ExcludeFromStatistics, onExcludeFromStatisticsChanged: ((ExcludeFromStatistics) -> Unit)?) {
	FormRadioGroup(
		title = stringResource(R.string.league_form_property_exclude),
		subtitle = stringResource(R.string.league_form_property_exclude_footer),
		options = ExcludeFromStatistics.values(),
		selected = excludeFromStatistics,
		titleForOption = {
			when (it) {
				ExcludeFromStatistics.INCLUDE -> stringResource(R.string.league_form_property_exclude_include)
				ExcludeFromStatistics.EXCLUDE -> stringResource(R.string.league_form_property_exclude_exclude)
			}
		},
		onOptionSelected = onExcludeFromStatisticsChanged ?: { }
	)
}

@Composable
internal fun GamesPerSeriesPicker(gamesPerSeries: GamesPerSeries, onGamesPerSeriesChanged: ((GamesPerSeries) -> Unit)?) {
	FormRadioGroup(
		title = stringResource(R.string.league_form_property_number_of_games),
		subtitle = stringResource(
			R.string.league_form_property_number_of_games_footer,
			stringResource(R.string.league_form_property_number_of_games_constant),
			stringResource(R.string.league_form_property_number_of_games_always_ask),
		),
		options = GamesPerSeries.values(),
		selected = gamesPerSeries,
		titleForOption = {
			when (it) {
				GamesPerSeries.STATIC -> stringResource(R.string.league_form_property_number_of_games_constant)
				GamesPerSeries.DYNAMIC -> stringResource(R.string.league_form_property_number_of_games_always_ask)
			}
		},
		onOptionSelected = onGamesPerSeriesChanged ?: { }
	)
}

@Composable
internal fun NumberOfGamesSlider(numberOfGames: Int, onNumberOfGamesChanged: ((Int) -> Unit)?) {
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

@Composable
internal fun IncludeAdditionalPinFallSwitch(includeAdditionalPinFall: IncludeAdditionalPinFall, onIncludeAdditionalPinFallChanged: ((IncludeAdditionalPinFall) -> Unit)?) {
	FormSwitch(
		titleResourceId = R.string.league_form_property_pinfall,
		isChecked = includeAdditionalPinFall == IncludeAdditionalPinFall.INCLUDE,
		onCheckChanged = {
			onIncludeAdditionalPinFallChanged?.invoke(if (it) IncludeAdditionalPinFall.INCLUDE else IncludeAdditionalPinFall.NONE)
		},
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp)
	)
}

@Composable
internal fun AdditionalPinFallField(additionalPinFall: Int, onAdditionalPinFallChanged: ((Int) -> Unit)?) {
	OutlinedTextField(
		value = additionalPinFall.toString(),
		onValueChange = {
			val intValue = it.toIntOrNull() ?: 0
			onAdditionalPinFallChanged?.invoke(intValue)
		},
		label = { Text(stringResource(R.string.league_form_property_pinfall_additional_pinfall)) },
		singleLine = true,
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp)
	)
}

@Composable
internal fun AdditionalGamesField(additionalGames: Int, onAdditionalGamesChanged: ((Int) -> Unit)?) {
	OutlinedTextField(
		value = additionalGames.toString(),
		onValueChange = {
			val intValue = it.toIntOrNull() ?: 0
			onAdditionalGamesChanged?.invoke(intValue)
		},
		label = { Text(stringResource(R.string.league_form_property_pinfall_additional_games)) },
		singleLine = true,
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp)
	)
}

@Preview
@Composable
fun LeagueFormPreview() {
	Surface {
		LeagueForm(
			name = "Majors 2022/2023",
			nameErrorId = null,
			excludeFromStatistics = ExcludeFromStatistics.INCLUDE,
			includeAdditionalPinFall = IncludeAdditionalPinFall.INCLUDE,
			numberOfGames = 4,
			additionalPinFall = 860,
			additionalGames = 4,
			recurrence = LeagueRecurrence.REPEATING,
			gamesPerSeries = GamesPerSeries.STATIC,
		)
	}
}