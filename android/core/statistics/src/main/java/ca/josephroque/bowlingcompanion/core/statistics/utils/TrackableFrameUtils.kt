package ca.josephroque.bowlingcompanion.core.statistics.utils

import ca.josephroque.bowlingcompanion.core.model.Frame
import ca.josephroque.bowlingcompanion.core.model.Pin
import ca.josephroque.bowlingcompanion.core.model.TrackableFrame
import ca.josephroque.bowlingcompanion.core.model.arePinsCleared

val TrackableFrame.firstRolls: List<TrackableFrame.Roll>
	get() {
		val firstRoll = rolls.firstOrNull() ?: return emptyList()
		return if (Frame.isLastFrame(index)) {
			val firstRolls = mutableListOf(firstRoll)
			var pinsDowned = mutableSetOf<Pin>()
			rolls.withIndex().forEach { (index, roll) ->
				pinsDowned += roll.pinsDowned
				if (pinsDowned.arePinsCleared() && index < rolls.lastIndex) {
					firstRolls.add(rolls[index + 1])
					pinsDowned = mutableSetOf()
				}
			}

			firstRolls
		} else {
			listOf(firstRoll)
		}
	}

val TrackableFrame.secondRolls: List<TrackableFrame.Roll>
	get() {
		val secondRoll = rolls.getOrNull(1) ?: return emptyList()
		return if (Frame.isLastFrame(index)) {
			val secondRolls = mutableListOf<TrackableFrame.Roll>()
			var pinsDowned = mutableSetOf<Pin>()
			var pinsJustCleared = true
			rolls.withIndex().forEach { (index, roll) ->
				pinsDowned += roll.pinsDowned
				if (pinsDowned.arePinsCleared()) {
					pinsJustCleared = true
					pinsDowned = mutableSetOf()
				} else {
					if (pinsJustCleared && index < rolls.lastIndex) {
						secondRolls.add(rolls[index + 1])
					}
					pinsJustCleared = false
				}
			}

			return secondRolls
		} else {
			listOf(secondRoll)
		}
	}

data class RollPair(
	val firstRoll: TrackableFrame.Roll,
	val secondRoll: TrackableFrame.Roll,
)

val TrackableFrame.rollPairs: List<RollPair>
	get() {
		val firstRolls = firstRolls
		val secondRolls = secondRolls
		return secondRolls.mapNotNull {
			val matchingFirstRoll = firstRolls.firstOrNull { firstRoll ->
				firstRoll.index == it.index - 1
			} ?: return@mapNotNull null

			if (matchingFirstRoll.pinsDowned.arePinsCleared()) {
				return@mapNotNull null
			}

			RollPair(matchingFirstRoll, it)
		}
	}

val TrackableFrame.pinsLeftOnDeck: Set<Pin>
	get() = if (Frame.isLastFrame(index)) {
		val pinsStanding = Pin.fullDeck().toMutableSet()
		rolls.forEachIndexed { index, roll ->
			pinsStanding -= roll.pinsDowned
			if (pinsStanding.isEmpty() && index < Frame.NumberOfRolls - 1) {
				pinsStanding.addAll(Pin.fullDeck())
			}
		}
		pinsStanding
	} else {
		rolls.fold(Pin.fullDeck().toMutableSet()) { pinsStanding, roll ->
			pinsStanding -= roll.pinsDowned
			pinsStanding
		}
	}