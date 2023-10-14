package ca.josephroque.bowlingcompanion.core.components

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
	checked: Boolean,
	onCheckedChange: (Boolean?) -> Unit,
	@StringRes titleResourceId: Int,
	modifier: Modifier = Modifier,
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween,
		modifier = modifier
			.fillMaxWidth()
			.clickable(onClick = { onCheckedChange(null) })
			.padding(vertical = 8.dp, horizontal = 16.dp)
	) {
		Text(
			text = stringResource(titleResourceId),
			style = MaterialTheme.typography.bodyMedium,
			modifier = Modifier.weight(1f),
		)

		Switch(
			checked = checked,
			onCheckedChange = onCheckedChange,
		)
	}
}