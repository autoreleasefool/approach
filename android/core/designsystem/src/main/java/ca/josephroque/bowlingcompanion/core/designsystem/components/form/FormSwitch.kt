package ca.josephroque.bowlingcompanion.core.designsystem.components.form

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun FormSwitch(
	@StringRes titleResourceId: Int,
	isChecked: Boolean,
	onCheckChanged: (Boolean) -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier,
	) {
		Text(
			text = stringResource(titleResourceId),
			style = MaterialTheme.typography.titleMedium,
			modifier = Modifier.weight(1f),
		)

		Box(modifier = Modifier.padding(8.dp)) {
			Switch(
				checked = isChecked,
				onCheckedChange = onCheckChanged,
			)
		}
	}
}
