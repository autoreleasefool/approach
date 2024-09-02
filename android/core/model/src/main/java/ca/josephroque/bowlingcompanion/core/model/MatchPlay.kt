package ca.josephroque.bowlingcompanion.core.model

import android.os.Parcelable
import java.util.UUID
import kotlinx.parcelize.Parcelize

@JvmInline
@Parcelize
value class MatchPlayID(val value: UUID) : Parcelable {
	override fun toString(): String = value.toString()
	companion object {
		fun randomID(): MatchPlayID = MatchPlayID(UUID.randomUUID())
		fun fromString(string: String): MatchPlayID = MatchPlayID(UUID.fromString(string))
	}
} data class MatchPlayCreate(
	val id: MatchPlayID,
	val gameId: GameID,
	val opponentId: BowlerID?,
	val opponentScore: Int?,
	val result: MatchPlayResult?,
)

data class MatchPlayUpdate(
	val id: MatchPlayID,
	val opponent: BowlerSummary?,
	val opponentScore: Int?,
	val result: MatchPlayResult?,
) {
	data class Properties(
		val id: MatchPlayID,
		val opponentId: BowlerID?,
		val opponentScore: Int?,
		val result: MatchPlayResult?,
	)
}

enum class MatchPlayResult {
	WON,
	LOST,
	TIED,
}
