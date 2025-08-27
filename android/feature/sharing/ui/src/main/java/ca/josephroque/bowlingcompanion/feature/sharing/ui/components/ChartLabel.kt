package ca.josephroque.bowlingcompanion.feature.sharing.ui.components

import androidx.annotation.ColorRes
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ChartLabel(
	icon: Painter,
	title: String,
	appearance: ChartLabelAppearance,
	modifier: Modifier = Modifier,
) {
	Row(
		modifier = modifier
			.clip(RoundedCornerShape(8.dp))
			.background(colorResource(appearance.backgroundColor))
			.padding(horizontal = 8.dp, vertical = appearance.style.padding),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(appearance.style.spacing),
	) {
		Image(
			painter = icon,
			contentDescription = null,
			colorFilter = ColorFilter.tint(color = colorResource(appearance.foregroundColor)),
			modifier = Modifier.size(appearance.style.iconSize),
		)

		Text(
			text = title,
			color = colorResource(appearance.foregroundColor),
			style = appearance.style.getTextStyle(),
		)
	}
}

@Composable
fun ChartLabel(
	content: ChartLabelContent,
	appearance: ChartLabelAppearance,
	modifier: Modifier = Modifier,
) {
	ChartLabel(
		icon = content.icon,
		title = content.title,
		appearance = appearance,
		modifier = modifier,
	)
}

data class ChartLabelContent(
	val icon: Painter,
	val title: String,
)

data class ChartLabelAppearance(
	val style: Style,
	@ColorRes val foregroundColor: Int,
	@ColorRes val backgroundColor: Int,
) {
	enum class Style(
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

	companion object {
		val DEFAULT = ChartLabelAppearance(
			foregroundColor = ca.josephroque.bowlingcompanion.core.scoresheet.R.color.scoresheet_default_text_on_background,
			backgroundColor = ca.josephroque.bowlingcompanion.core.scoresheet.R.color.scoresheet_default_background,
			style = Style.PLAIN,
		)
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
				appearance  = ChartLabelAppearance.DEFAULT.copy(style = ChartLabelAppearance.Style.TITLE),
			)

			ChartLabel(
				icon = rememberVectorPainter(Icons.Default.KeyboardArrowUp),
				title = "420 HIGH",
				appearance = ChartLabelAppearance.DEFAULT,
			)

			ChartLabel(
				icon = rememberVectorPainter(Icons.Default.KeyboardArrowDown),
				title = "200 LOW",
				appearance = ChartLabelAppearance.DEFAULT.copy(style = ChartLabelAppearance.Style.SMALL),
			)
		}
	}
}
