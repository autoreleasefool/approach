package ca.josephroque.bowlingcompanion.core.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.R

@Composable
fun BackButton(
	onClick: () -> Unit
) {
	IconButton(onClick = onClick) {
		Icon(
			Icons.Default.ArrowBack,
			contentDescription = stringResource(R.string.cd_back),
			tint = MaterialTheme.colorScheme.onSurface,
		)
	}
}