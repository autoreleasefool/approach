package ca.josephroque.bowlingcompanion.core.model

data class Avatar(
	val primaryColor: RGB,
	val secondaryColor: RGB,
) {
	data class RGB(
		val red: Int,
		val green: Int,
		val blue: Int,
	)

	companion object {
		fun default(): Avatar = Avatar(
			primaryColor = RGB(0, 0, 0),
			secondaryColor = RGB(255, 255, 255),
		)
	}
}