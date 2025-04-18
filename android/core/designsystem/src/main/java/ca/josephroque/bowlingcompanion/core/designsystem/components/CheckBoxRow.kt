package ca.josephroque.bowlingcompanion.core.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.R

@Composable
fun CheckBoxRow(
	isSelected: Boolean,
	onClick: () -> Unit,
	content: @Composable BoxScope.() -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier
			.clickable(onClick = onClick)
			.padding(16.dp),
	) {
		if (isSelected) {
			Icon(
				painter = painterResource(
					R.drawable.ic_check_box,
				),
				contentDescription = stringResource(
					R.string.cd_resource_selected,
				),
				tint = colorResource(R.color.purple_500),
			)
		} else {
			Icon(
				painter = painterResource(
					R.drawable.ic_check_box_outline,
				),
				contentDescription = stringResource(
					R.string.cd_resource_deselected,
				),
				tint = MaterialTheme.colorScheme.onSurface,
			)
		}

		Box(modifier = Modifier.weight(1f)) {
			content()
		}
	}
}

@Preview
@Composable
private fun CheckBoxRowPreview() {
	Surface {
		Column {
			CheckBoxRow(
				isSelected = true,
				onClick = {},
				content = {
					Text(text = "Selected")
				},
			)

			CheckBoxRow(
				isSelected = false,
				onClick = {},
				content = {
					Text(text = "Unselected")
				},
			)
		}
	}
}
