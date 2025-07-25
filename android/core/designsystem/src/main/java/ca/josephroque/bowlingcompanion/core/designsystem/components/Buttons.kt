package ca.josephroque.bowlingcompanion.core.designsystem.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.core.designsystem.R

@Composable
fun BackButton(onClick: () -> Unit) {
	IconButton(onClick = onClick) {
		Icon(
			Icons.AutoMirrored.Filled.ArrowBack,
			contentDescription = stringResource(R.string.cd_back),
			tint = MaterialTheme.colorScheme.onSurface,
		)
	}
}

@Composable
fun CloseButton(onClick: () -> Unit) {
	IconButton(onClick = onClick) {
		Icon(
			Icons.Default.Close,
			contentDescription = stringResource(R.string.cd_close),
			tint = MaterialTheme.colorScheme.onSurface,
		)
	}
}

@Composable
fun CancelButton(onClick: () -> Unit) {
	IconButton(onClick = onClick) {
		Icon(
			Icons.Default.Clear,
			contentDescription = stringResource(R.string.action_cancel),
			tint = MaterialTheme.colorScheme.onSurface,
		)
	}
}