package ca.josephroque.bowlingcompanion.core.scoresheet

import androidx.annotation.ColorRes
import androidx.annotation.StringRes

enum class FramePosition {
	TOP,
	BOTTOM,
}

enum class ScorePosition {
	START,
	END,
}

enum class GameIndexPosition {
	START,
}

data class ScoreSheetConfiguration(
	val style: Style = Style.PLAIN,
	val framePosition: Set<FramePosition> = setOf(FramePosition.BOTTOM),
	val scorePosition: Set<ScorePosition> = setOf(ScorePosition.END),
	val gameIndexPosition: Set<GameIndexPosition> = emptySet(),
	val scrollEnabled: Boolean = true,
	val relativeContainerSizing: Boolean = true,
) {
	enum class Style(
		@param:StringRes val title: Int,
		@param:ColorRes val textColorOnBackground: Int,
		@param:ColorRes val textSecondaryColorOnBackground: Int,
		@param:ColorRes val textFoulColorOnBackground: Int,
		@param:ColorRes val textHighlightColorOnBackground: Int,
		@param:ColorRes val textHighlightSecondaryColorOnBackground: Int,
		@param:ColorRes val textHighlightFoulColorOnBackground: Int,
		@param:ColorRes val textColorOnRail: Int,
		@param:ColorRes val textHighlightColorOnRail: Int,
		@param:ColorRes val textColorOnCard: Int,
		@param:ColorRes val backgroundColor: Int,
		@param:ColorRes val backgroundHighlightColor: Int,
		@param:ColorRes val railBackgroundColor: Int,
		@param:ColorRes val railBackgroundHighlightColor: Int,
		@param:ColorRes val cardBackgroundColor: Int,
		@param:ColorRes val borderColor: Int,
		@param:ColorRes val strongBorderColor: Int,
	) {
		PLAIN(
			title = R.string.scoresheet_style_plain_title,
			textColorOnBackground = R.color.scoresheet_default_text_on_background,
			textSecondaryColorOnBackground = R.color.scoresheet_default_text_secondary_on_background,
			textFoulColorOnBackground = R.color.scoresheet_default_text_foul_on_background,
			textHighlightColorOnBackground = R.color.scoresheet_default_text_highlight_on_background,
			textHighlightSecondaryColorOnBackground =
			R.color.scoresheet_default_text_highlight_secondary_on_background,
			textHighlightFoulColorOnBackground =
			R.color.scoresheet_default_text_highlight_foul_on_background,
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
		),
	}

	companion object {
		val DEFAULT = ScoreSheetConfiguration()
		val LIST_DEFAULT = ScoreSheetConfiguration(
			style = Style.PLAIN,
			framePosition = setOf(FramePosition.BOTTOM),
			scorePosition = setOf(ScorePosition.START, ScorePosition.END),
		)
	}
}
