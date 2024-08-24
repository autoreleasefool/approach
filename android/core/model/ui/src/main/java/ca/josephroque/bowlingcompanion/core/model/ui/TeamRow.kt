package ca.josephroque.bowlingcompanion.core.model.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.model.ui.utils.formatAsAverage

@Composable
fun TeamRow(
	name: String,
	members: List<String>,
	modifier: Modifier = Modifier,
	average: Double? = null,
	onClick: (() -> Unit)? = null,
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(16.dp),
		modifier = modifier
			.fillMaxWidth()
			.then(
				if (onClick != null) {
					Modifier
						.clickable(onClick = onClick)
						.padding(16.dp)
				} else {
					Modifier
				},
			),
	) {
		Column(
			horizontalAlignment = Alignment.Start,
			verticalArrangement = Arrangement.spacedBy(4.dp),
			modifier = Modifier.weight(1f),
		) {
			Text(
				text = name,
				style = MaterialTheme.typography.titleMedium,
			)

			Text(
				text = members.teamMemberNames(),
				style = MaterialTheme.typography.bodyMedium,
			)
		}

		average?.let {
			Text(
				text = it.formatAsAverage(),
				style = MaterialTheme.typography.bodyLarge,
				maxLines = 1,
			)
		}
	}
}

@Composable
fun List<String>.teamMemberNames(): String = when (this.size) {
	0 -> ""
	1 -> this[0]
	2 -> stringResource(R.string.team_member_names_for_2, this[0], this[1])
	else -> stringResource(R.string.team_member_names_for_more, this[0], this[1], this.size - 2)
}

@Preview
@Composable
private fun TeamRowPreview() {
	Surface {
		Column {
			TeamRow(
				name = "Team Name",
				members = listOf("Joseph"),
				average = 200.0,
				onClick = {},
			)

			TeamRow(
				name = "Team Name",
				members = listOf("Joseph", "John"),
				average = 200.0,
				onClick = {},
			)

			TeamRow(
				name = "Team Name",
				members = listOf("Joseph", "John", "Jane"),
				average = 200.0,
				onClick = {},
			)
		}
	}
}
