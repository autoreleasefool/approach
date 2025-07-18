package ca.josephroque.bowlingcompanion.feature.sharing.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingAppearance

@Composable
fun ChartLabel(
	icon: Painter,
	title: String,
	style: ChartLabelStyle,
	appearance: SharingAppearance,
	modifier: Modifier = Modifier,
) {
	Row(
		modifier = modifier
			.clip(RoundedCornerShape(8.dp))
			.background(
				when (appearance) {
					SharingAppearance.Dark -> Color.Gray.copy(alpha = 0.8f)
					SharingAppearance.Light -> Color.Black.copy(alpha = 0.2f)
				},
			)
			.padding(horizontal = 8.dp, vertical = style.padding),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(style.spacing),
	) {
		Image(
			painter = icon,
			contentDescription = null,
			colorFilter = ColorFilter.tint(
				color = when (appearance) {
					SharingAppearance.Dark -> Color.White.copy(alpha = 0.9f)
					SharingAppearance.Light -> Color.Black
				},
			),
			modifier = Modifier.size(style.iconSize),
		)

		Text(
			text = title,
			color = when (appearance) {
				SharingAppearance.Dark -> Color.White.copy(alpha = 0.9f)
				SharingAppearance.Light -> Color.Black
			},
			style = style.getTextStyle(),
		)
	}
}

enum class ChartLabelStyle(
	val iconSize: Dp,
	val spacing: Dp,
	val padding: Dp,
) {
	TITLE(
		iconSize = 20.dp,
		spacing = 8.dp,
		padding = 8.dp,
	),

	PLAIN(
		iconSize = 20.dp,
		spacing = 8.dp,
		padding = 4.dp,
	),

	SMALL(
		iconSize = 12.dp,
		spacing = 8.dp,
		padding = 4.dp,
	),
	;

	@Composable
	fun getTextStyle(): TextStyle = when (this) {
		TITLE -> MaterialTheme.typography.titleSmall
		PLAIN -> MaterialTheme.typography.bodyMedium
		SMALL -> MaterialTheme.typography.labelMedium
	}
}

@Preview
@Composable
fun ChartLabelPreview() {
	Surface {
		Row {
			ChartLabel(
				icon = rememberVectorPainter(Icons.Default.Star),
				title = "450 TOTAL",
				style = ChartLabelStyle.TITLE,
				appearance = SharingAppearance.Dark,
			)

			ChartLabel(
				icon = rememberVectorPainter(Icons.Default.KeyboardArrowUp),
				title = "420 HIGH",
				style = ChartLabelStyle.PLAIN,
				appearance = SharingAppearance.Light,
			)

			ChartLabel(
				icon = rememberVectorPainter(Icons.Default.KeyboardArrowDown),
				title = "200 LOW",
				style = ChartLabelStyle.SMALL,
				appearance = SharingAppearance.Light,
			)
		}
	}
}
