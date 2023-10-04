package ca.josephroque.bowlingcompanion.core.components.form

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import ca.josephroque.bowlingcompanion.feature.leagueform.ui.stringForRecurrence

@Composable
fun FormPicker(
	title: String,
	modifier: Modifier = Modifier,
	subtitle: String? = null,
) {
	Text(
		text = title,
		modifier = Modifier.padding(horizontal = 16.dp),
	)

	subtitle?.let {
		Text(
			text = subtitle,
			fontWeight = FontWeight.Light,
			fontSize = 12.sp,
			modifier = Modifier
				.padding(horizontal = 16.dp, vertical = 8.dp),
		)
	}

//	LeagueRecurrence.values().forEach {
//		val selected = it == recurrence
//
//		Surface(
//			color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
//			modifier = Modifier
//				.selectable(
//					selected,
//					onClick = { onRecurrenceChanged?.invoke(it) },
//					role = Role.RadioButton,
//				)
//		) {
//			Row(
//				modifier = Modifier
//					.fillMaxWidth()
//					.padding(16.dp),
//				verticalAlignment = Alignment.CenterVertically,
//			) {
//				Text(
//					text = stringForRecurrence(it),
//					modifier = Modifier.weight(1f),
//				)
//				Box(Modifier.padding(8.dp)) {
//					RadioButton(selected, onClick = null)
//				}
//			}
//		}
//	}
}