package ca.josephroque.bowlingcompanion.core.designsystem.components

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
fun LabeledSwitch(
	@StringRes titleResourceId: Int,
	checked: Boolean,
	onCheckedChange: (Boolean?) -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	@StringRes subtitleResourceId: Int? = null,
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		modifier = modifier
			.fillMaxWidth()
			.clickable(onClick = { onCheckedChange(null) }, enabled = enabled)
			.padding(vertical = 8.dp, horizontal = 16.dp)
	) {
		Column(
			horizontalAlignment = Alignment.Start,
			verticalArrangement = Arrangement.spacedBy(4.dp),
			modifier = modifier
				.weight(1f)
		) {
			Text(
				text = stringResource(titleResourceId),
				style = MaterialTheme.typography.titleMedium,
			)

			subtitleResourceId?.let {
				Text(
					text = stringResource(subtitleResourceId),
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
			}
		}


		Switch(
			checked = checked,
			onCheckedChange = onCheckedChange,
			enabled = enabled,
		)
	}
}