package ca.josephroque.bowlingcompanion.feature.seriesform.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.common.utils.simpleFormat
import ca.josephroque.bowlingcompanion.core.designsystem.R
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesDatePicker(
	@StringRes label: Int,
	currentDate: LocalDate,
	isDatePickerVisible: Boolean,
	onDateChanged: (LocalDate) -> Unit,
	onDateClicked: () -> Unit,
	onDatePickerDismissed: () -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
) {
	val initialSelection = remember(currentDate) {
		currentDate
			.atStartOfDayIn(TimeZone.currentSystemDefault())
			.toEpochMilliseconds()
	}

	val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialSelection)

	val onDismiss = {
		datePickerState.selectedDateMillis = initialSelection
		onDatePickerDismissed()
	}

	if (isDatePickerVisible) {
		DatePickerDialog(
			onDismissRequest = onDismiss,
			confirmButton = {
				TextButton(
					onClick = {
						val currentSelectedDate = datePickerState.selectedDateMillis ?: return@TextButton
						onDateChanged(
							Instant.fromEpochMilliseconds(currentSelectedDate)
								.toLocalDateTime(TimeZone.UTC)
								.date,
						)
					},
				) {
					Text(stringResource(R.string.action_confirm))
				}
			},
			dismissButton = {
				TextButton(onClick = onDismiss) {
					Text(stringResource(R.string.action_cancel))
				}
			},
		) {
			DatePicker(state = datePickerState)
		}
	}

	OutlinedTextField(
		label = { Text(stringResource(label)) },
		value = currentDate.simpleFormat(),
		onValueChange = {},
		enabled = false,
		leadingIcon = {
			Icon(
				painter = painterResource(
					R.drawable.ic_event,
				),
				contentDescription = null,
			)
		},
		trailingIcon = {
			if (enabled) {
				Icon(
					Icons.Default.Edit,
					contentDescription = null,
				)
			}
		},
		colors = OutlinedTextFieldDefaults.colors(
			disabledTextColor = MaterialTheme.colorScheme.onSurface,
			disabledBorderColor = MaterialTheme.colorScheme.outline,
			disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
			disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
			disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
			disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
		),
		modifier = modifier
			.fillMaxWidth()
			.clickable(onClick = onDateClicked, enabled = enabled)
			.padding(horizontal = 16.dp),
	)
}
