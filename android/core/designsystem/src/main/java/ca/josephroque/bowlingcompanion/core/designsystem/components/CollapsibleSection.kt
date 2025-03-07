package ca.josephroque.bowlingcompanion.core.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.R

@Composable
fun CollapsibleSection(
	title: String,
	footer: String? = null,
	sectionContent: @Composable ColumnScope.() -> Unit,
) {
	val isExpanded = remember { mutableStateOf(false) }
	val rotationAngle by animateFloatAsState(targetValue = if (isExpanded.value) 180f else 0f)

	Column {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier
				.clickable(onClick = { isExpanded.value = !isExpanded.value })
				.fillMaxWidth(),
		) {
			SectionHeader(
				title = title,
				modifier = Modifier.weight(1f, fill = false),
			)

			Icon(
				imageVector = Icons.Filled.KeyboardArrowDown,
				contentDescription = if (isExpanded.value) stringResource(R.string.action_collapse) else stringResource(R.string.action_expand),
				modifier = Modifier
					.padding(end = 16.dp)
					.graphicsLayer(rotationZ = rotationAngle)
			)
		}

		AnimatedVisibility(
			visible = isExpanded.value,
			enter = expandVertically() + fadeIn(),
			exit = shrinkVertically() + fadeOut(),
		) {
			sectionContent()
		}

		footer?.let {
			SectionFooter(
				footer = it,
				modifier = Modifier.padding(horizontal = 16.dp),
			)
		}
	}
}

@Preview
@Composable
private fun CollapsibleSectionPreview() {
	Surface {
		CollapsibleSection(
			title = "Collapsible Section",
			sectionContent = {
				Text(
					text = "This is the content of the collapsible section",
					modifier = Modifier.padding(16.dp)
				)
			}
		)
	}
}