package ca.josephroque.bowlingcompanion.core.scoresheet

import androidx.annotation.ColorRes
import androidx.annotation.StringRes

data class ScoreSheetConfiguration(
	val style: Style,
) {

	enum class Style(
		@StringRes val title: Int,
		@ColorRes val textColorOnBackground: Int,
		@ColorRes val textFoulColorOnBackground: Int,
		@ColorRes val textHighlightColorOnBackground: Int,
		@ColorRes val textHighlightFoulColorOnBackground: Int,
		@ColorRes val textColorOnRail: Int,
		@ColorRes val textHighlightColorOnRail: Int,
		@ColorRes val textColorOnCard: Int,
		@ColorRes val backgroundColor: Int,
		@ColorRes val backgroundHighlightColor: Int,
		@ColorRes val railBackgroundColor: Int,
		@ColorRes val railBackgroundHighlightColor: Int,
		@ColorRes val cardBackgroundColor: Int,
		@ColorRes val borderColor: Int,
		@ColorRes val strongBorderColor: Int,
	) {
		PLAIN(
			title = R.string.scoresheet_style_plain_title,
			textColorOnBackground = R.color.scoresheet_default_text_on_background,
			textFoulColorOnBackground = R.color.scoresheet_default_text_foul_on_background,
			textHighlightColorOnBackground = R.color.scoresheet_default_text_highlight_on_background,
			textHighlightFoulColorOnBackground = R.color.scoresheet_default_text_highlight_foul_on_background,
			textColorOnRail = R.color.scoresheet_default_text_on_rail,
			textHighlightColorOnRail = R.color.scoresheet_default_text_highlight_on_rail,
			textColorOnCard = R.color.scoresheet_default_text_on_card,
			backgroundColor = R.color.scoresheet_default_background,
			backgroundHighlightColor = R.color.scoresheet_default_background_highlight,
			railBackgroundColor = R.color.scoresheet_default_rail_background,
			railBackgroundHighlightColor = R.color.scoresheet_default_rail_background_highlight,
			cardBackgroundColor = R.color.scoresheet_default_card_background,
			borderColor = R.color.scoresheet_default_border,
			strongBorderColor = R.color.scoresheet_default_border_strong,
		)
	}
}

