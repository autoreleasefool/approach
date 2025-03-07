package ca.josephroque.bowlingcompanion.feature.sharing.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.feature.sharing.ui.R

@Composable
fun FilterItem(@StringRes title: Int, selected: Boolean, onClick: () -> Unit, imageVector: ImageVector) {
	FilterChip(
		selected = selected,
		onClick = onClick,
		label = {
			Text(
				text = stringResource(title),
				style = MaterialTheme.typography.bodyMedium,
				fontWeight = FontWeight.SemiBold,
			)
		},
		trailingIcon = {
			Icon(
				imageVector,
				contentDescription = null,
				modifier = Modifier.size(18.dp),
			)
		},
	)
}

@Preview
@Composable
private fun FilterItemPreview() {
	FilterItem(
		title = R.string.sharing_series_modifier_date,
		selected = true,
		onClick = {},
		imageVector = Icons.Default.DateRange,
	)
}
