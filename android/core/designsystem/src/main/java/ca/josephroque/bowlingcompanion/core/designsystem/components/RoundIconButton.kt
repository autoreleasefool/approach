package ca.josephroque.bowlingcompanion.core.designsystem.components

import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import ca.josephroque.bowlingcompanion.core.designsystem.R

@Composable
fun RoundIconButton(
	modifier: Modifier = Modifier,
	onClick: () -> Unit,
	icon: @Composable () -> Unit,
) {
	FilledIconToggleButton(
		checked = true,
		onCheckedChange = { onClick() },
		modifier = modifier,
		colors = IconButtonDefaults.iconToggleButtonColors(
			checkedContainerColor = MaterialTheme.colorScheme.primaryContainer,
			checkedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
		),
	) {
		icon()
	}
}

@Preview
@Composable
private fun RoundIconButtonPreview() {
	RoundIconButton(
		icon = {
			Icon(
				painter = painterResource(R.drawable.ic_settings),
				contentDescription = null,
				)
		},
		onClick = {},
	)
}