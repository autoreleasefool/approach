package ca.josephroque.bowlingcompanion.core.model

data class Avatar(
	val label: String,
	val primaryColor: RGB,
	val secondaryColor: RGB,
) {
	data class RGB(
		val red: Int,
		val green: Int,
		val blue: Int,
	) {
		override fun toString(): String = "$red,$green,$blue"

		companion object
	}

	companion object {
		fun default(): Avatar = Avatar(
			label = "Hp",
			primaryColor = RGB(85, 60, 190),
			secondaryColor = RGB(110, 98, 215),
		)

		fun fromString(value: String): Avatar {
			val (label, primary, secondary) = value.split(";")
			val (pRed, pGreen, pBlue) = primary.split(",")
			val (sRed, sGreen, sBlue) = secondary.split(",")
			return Avatar(
				label = label,
				primaryColor = RGB(pRed.toInt(), pGreen.toInt(), pBlue.toInt()),
				secondaryColor = RGB(sRed.toInt(), sGreen.toInt(), sBlue.toInt()),
			)
		}
	}

	override fun toString(): String = "$label;$primaryColor;$secondaryColor"
}
