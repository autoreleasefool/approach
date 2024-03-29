package ca.josephroque.bowlingcompanion.core.designsystem.components.form

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun <T> FormRadioGroup(
	@StringRes titleResourceId: Int,
	options: Array<T>,
	selected: T?,
	titleForOption: @Composable (T?) -> String,
	onOptionSelected: (T?) -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	@StringRes subtitleResourceId: Int? = null,
	@StringRes errorResourceId: Int? = null,
) {
	FormRadioGroup(
		title = stringResource(titleResourceId),
		options = options,
		selected = selected,
		titleForOption = titleForOption,
		onOptionSelected = onOptionSelected,
		modifier = modifier,
		subtitle = subtitleResourceId?.let { stringResource(it) },
		error = errorResourceId?.let { stringResource(it) },
		enabled = enabled,
	)
}

@Composable
fun <T> FormRadioGroup(
	title: String,
	options: Array<T>,
	selected: T?,
	titleForOption: @Composable (T?) -> String,
	onOptionSelected: (T?) -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	iconForOption: (@Composable (T?) -> Unit)? = null,
	allowNullableSelection: Boolean = false,
	subtitle: String? = null,
	error: String? = null,
) {
	Column(modifier = modifier) {
		Text(
			text = title,
			style = MaterialTheme.typography.titleMedium,
			modifier = Modifier.padding(horizontal = 16.dp),
		)

		subtitle?.let {
			Text(
				text = subtitle,
				style = MaterialTheme.typography.bodySmall,
				modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
			)
		}

		if (allowNullableSelection) {
			FormRadioButton(
				isSelected = selected == null,
				title = titleForOption(null),
				icon = { iconForOption?.invoke(null) },
				enabled = enabled,
				onClick = { onOptionSelected(null) },
			)
		}

		options.forEach {
			val isSelected = it == selected

			FormRadioButton(
				isSelected = isSelected,
				title = titleForOption(it),
				icon = { iconForOption?.invoke(it) },
				enabled = enabled,
				onClick = { onOptionSelected(it) },
			)
		}

		error?.let {
			Text(
				text = error,
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.error,
				modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
			)
		}
	}
}

@Composable
private fun FormRadioButton(
	isSelected: Boolean,
	title: String,
	icon: @Composable () -> Unit,
	enabled: Boolean,
	onClick: () -> Unit,
) {
	Surface(
		color = if (isSelected) {
			MaterialTheme.colorScheme.primaryContainer
		} else {
			MaterialTheme.colorScheme.surface
		},
		modifier = Modifier
			.selectable(
				selected = isSelected,
				onClick = onClick,
				enabled = enabled,
				role = Role.RadioButton,
			),
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(16.dp),
			modifier = Modifier
				.fillMaxWidth()
				.defaultMinSize(minHeight = 44.dp)
				.padding(horizontal = 16.dp, vertical = 8.dp),
		) {
			icon()

			Text(
				text = title,
				style = MaterialTheme.typography.bodyLarge,
				color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (enabled) 1f else 0.5f),
				modifier = Modifier.weight(1f),
			)

			Box(modifier = Modifier.padding(horizontal = 8.dp)) {
				RadioButton(selected = isSelected, onClick = null, enabled = enabled)
			}
		}
	}
}
