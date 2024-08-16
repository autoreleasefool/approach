package ca.josephroque.bowlingcompanion.core.scoring

import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers.Default
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.model.Frame
import ca.josephroque.bowlingcompanion.core.model.Game
import ca.josephroque.bowlingcompanion.core.model.Pin
import ca.josephroque.bowlingcompanion.core.model.Roll
import ca.josephroque.bowlingcompanion.core.model.ScoringFrame
import ca.josephroque.bowlingcompanion.core.model.ScoringRoll
import ca.josephroque.bowlingcompanion.core.model.arePinsCleared
import ca.josephroque.bowlingcompanion.core.model.displayAt
import ca.josephroque.bowlingcompanion.core.model.gameScore
import ca.josephroque.bowlingcompanion.core.model.pinCount
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class FivePinScoreKeeper @Inject constructor(
	@Dispatcher(Default) private val defaultDispatcher: CoroutineDispatcher,
) : ScoreKeeper {
	override suspend fun calculateHighestScorePossible(input: ScoreKeeperInput): Int =
		withContext(defaultDispatcher) {
			calculateHighestScorePossibleInternal(input)
		}

	override suspend fun calculateScore(input: ScoreKeeperInput): List<ScoringFrame> =
		withContext(defaultDispatcher) {
			calculateScoreInternal(input)
		}

	private fun calculateHighestScorePossibleInternal(input: ScoreKeeperInput): Int {
		val scoredFrames = calculateScoreInternal(input)
		val currentScore = scoredFrames.gameScore() ?: return Game.MAX_SCORE
		val lastValidFrame = input.rolls.indexOfLast { it.isNotEmpty() }
		if (lastValidFrame < 0) return Game.MAX_SCORE
		val pinValueForFrame = pinValueRemaining(input.rolls[lastValidFrame], lastValidFrame)
		val remainingFrameIndices = (lastValidFrame + 1)..<Game.NUMBER_OF_FRAMES
		return currentScore + pinValueForFrame + remainingFrameIndices.count() * 45
	}

	private fun pinValueRemaining(frame: List<ScoreKeeperInput.Roll>, frameIndex: Int): Int {
		if (frame.size == Frame.NUMBER_OF_ROLLS) return 0

		val pinsDown = mutableSetOf<Pin>()
		for (roll in frame) {
			pinsDown += roll.pinsDowned
			if (pinsDown.size == 5) {
				if (Frame.isLastFrame(frameIndex)) {
					pinsDown.clear()
				} else {
					return 0
				}
			}
		}

		val standingPinValue = Pin.fullDeck().pinCount() - pinsDown.pinCount()
		if (Frame.isLastFrame(frameIndex)) {
			val framesNeededForStandingPinValue = if (standingPinValue > 0) 1 else 0
			return Pin.fullDeck().pinCount() *
				(Frame.NUMBER_OF_ROLLS - frame.size - framesNeededForStandingPinValue) +
				standingPinValue
		} else {
			return standingPinValue + (if (frame.size == 1) 15 else 0)
		}
	}

	private fun calculateScoreInternal(input: ScoreKeeperInput): List<ScoringFrame> {
		val steps: MutableList<ScoringFrame> = mutableListOf()
		val state = generateStateFromInput(input)

		// Ensure there is at least one roll in the game, or return a nil score
		if (state.input.rolls.all { it.isEmpty() }) {
			return emptyGame()
		}

		// Calculate all except the final frame
		for ((frameIndex, frameRolls) in state.input.rolls.withIndex()) {
			if (Frame.isLastFrame(frameIndex)) continue

			val nextFrameRolls =
				getRollsForFrame(state.input.rolls, frameIndex = frameIndex + 1, atListIndex = frameIndex + 1)
			val nextNextFrameRolls =
				getRollsForFrame(state.input.rolls, frameIndex = frameIndex + 2, atListIndex = frameIndex + 2)
			steps.add(
				scoreFrame(
					frameIndex = frameIndex,
					frameRolls = frameRolls,
					nextFrameRolls = nextFrameRolls,
					nextNextFrameRolls = nextNextFrameRolls,
					state = state,
				),
			)
		}

		// Calculate the final frame separately
		scoreLastFrame(
			lastFrameRolls = state.input.rolls.lastOrNull {
				Frame.isLastFrame(it.firstOrNull()?.frameIndex ?: 0)
			} ?: emptyList(),
			state = state,
		)?.let {
			steps.add(it)
		}

		padFrames(frames = steps)
		return steps
	}

	private fun scoreFrame(
		frameIndex: Int,
		frameRolls: List<ScoreKeeperInput.Roll>,
		nextFrameRolls: List<ScoreKeeperInput.Roll>?,
		nextNextFrameRolls: List<ScoreKeeperInput.Roll>?,
		state: ScoreKeeperState,
	): ScoringFrame {
		// Cumulative set of pins downed in a frame
		val pinsDown = mutableSetOf<Pin>()
		// Each roll to be displayed in the final output
		val rollSteps = mutableListOf<ScoringRoll>()

		for (rollIndex in frameRolls.indices) {
			val roll = frameRolls[rollIndex]

			state.applyPenalty(applied = roll.didFoul)

			// Accumulate the downed pins
			pinsDown += roll.pinsDowned

			// When all the pins have been cleared before the last roll
			if (pinsDown.size == 5 && !Roll.isLastRoll(rollIndex)) {
				// Append a roll with the full deck cleared
				rollSteps.add(
					ScoringRoll(
						index = rollSteps.size,
						display = pinsDown.displayAt(rollIndex),
						didFoul = roll.didFoul,
						isSecondaryValue = false,
					),
				)

				state.accruingScore += pinsDown.pinCount()

				val (nextRoll, nextNextRoll) = getSubsequentRolls(nextFrameRolls, nextNextFrameRolls)
				val nextRolls = listOf(nextRoll, nextNextRoll).take(Frame.NUMBER_OF_ROLLS - rollIndex - 1)
				for (countedRoll in nextRolls) {
					countedRoll?.let {
						state.accruingScore += it.pinsDowned.pinCount()
						rollSteps.add(
							ScoringRoll(
								index = rollSteps.size,
								display = it.pinsDowned.displayAt(-1),
								didFoul = false,
								isSecondaryValue = true,
							),
						)
					}
				}
			} else {
				// Append the value of pins downed this roll
				rollSteps.add(
					ScoringRoll(
						index = rollSteps.size,
						display = roll.pinsDowned.displayAt(rollIndex),
						didFoul = roll.didFoul,
						isSecondaryValue = false,
					),
				)

				// For the last frame of a roll, add the total value of pins downed this frame to the score
				if (rollIndex == frameRolls.lastIndex) {
					state.accruingScore += pinsDown.pinCount()
				}
			}
		}

		val hasNextRoll = nextFrameRolls?.isNotEmpty() == true || nextNextFrameRolls?.isNotEmpty() == true
		padRolls(rollSteps, display = if (hasNextRoll) "-" else null)

		return ScoringFrame(
			index = frameIndex,
			rolls = rollSteps,
			score = state.displayScoreForFrame(frameIndex),
		)
	}

	private fun scoreLastFrame(
		lastFrameRolls: List<ScoreKeeperInput.Roll>,
		state: ScoreKeeperState,
	): ScoringFrame? {
		if (!Frame.isLastFrame(state.lastValidFrameIndex)) return null

		var stepScore = 0
		var initialRollIndex = 0

		val rollSteps = mutableListOf<ScoringRoll>()
		var pinsDown = mutableSetOf<Pin>()

		val ballsRolledInFinalFrame = lastFrameRolls.size
		var pinsDownedOnce = false

		for (roll in lastFrameRolls) {
			state.applyPenalty(applied = roll.didFoul)
			pinsDown += roll.pinsDowned

			// When all the pins have been cleared
			if (pinsDown.size == 5 &&
				!(
					!pinsDownedOnce &&
						ballsRolledInFinalFrame == 3 &&
						Roll.isLastRoll(
							roll.rollIndex,
						)
					)
			) {
				// Append a roll with the full deck cleared
				pinsDownedOnce = true
				rollSteps.add(
					ScoringRoll(
						index = rollSteps.size,
						display = pinsDown.displayAt(roll.rollIndex - initialRollIndex),
						didFoul = roll.didFoul,
						isSecondaryValue = false,
					),
				)

				stepScore += pinsDown.pinCount()
				pinsDown = mutableSetOf()

				// Treat the next roll after a cleared deck as a "first" roll
				initialRollIndex = roll.rollIndex + 1
			} else {
				// Append the value of pins downed this roll
				rollSteps.add(
					ScoringRoll(
						index = rollSteps.size,
						display = roll.pinsDowned.displayAt(rollIndex = roll.rollIndex - initialRollIndex),
						didFoul = roll.didFoul,
						isSecondaryValue = false,
					),
				)

				if (Roll.isLastRoll(roll.rollIndex)) {
					stepScore += pinsDown.pinCount()
				}
			}
		}

		state.accruingScore += stepScore
		padRolls(rollSteps, display = null)
		return ScoringFrame(
			index = Game.FrameIndices.last,
			rolls = rollSteps,
			score = state.displayScoreForFrame(Game.FrameIndices.last),
		)
	}

	private fun emptyGame(): List<ScoringFrame> = Game.FrameIndices.map { frameIndex ->
		ScoringFrame(
			index = frameIndex,
			rolls = Frame.RollIndices.map { rollIndex ->
				ScoringRoll(index = rollIndex, display = null, didFoul = false, isSecondaryValue = false)
			},
			score = null,
		)
	}

	private fun generateStateFromInput(input: ScoreKeeperInput): ScoreKeeperState {
		val lastValidFrameIndex = input.rolls.indexOfLast { it.isNotEmpty() }
		val cleanRolls = mutableListOf<List<ScoreKeeperInput.Roll>>()

		for ((index, frameRolls) in input.rolls.withIndex()) {
			val frameIndex = frameRolls.firstOrNull()?.frameIndex ?: index

			// Must be at least 1 roll or we skip the frame
			if (frameRolls.isEmpty()) {
				if (frameIndex < lastValidFrameIndex) {
					// Insert empty rolls for missing frames
					cleanRolls.add(
						Frame.RollIndices.map {
							ScoreKeeperInput.Roll(
								frameIndex = frameIndex,
								rollIndex = it,
								pinsDowned = setOf(),
								didFoul = false,
							)
						},
					)
				}
				continue
			}

			val cleanFrameRolls = mutableListOf<ScoreKeeperInput.Roll>()
			val pinsDowned = mutableSetOf<Pin>()
			for (rollIndex in frameRolls.indices) {
				val roll = frameRolls[rollIndex]
				pinsDowned += roll.pinsDowned
				cleanFrameRolls.add(
					ScoreKeeperInput.Roll(
						frameIndex,
						rollIndex,
						pinsDowned = roll.pinsDowned,
						didFoul = roll.didFoul,
					),
				)

				// Ignore any other rolls once all 5 pins are down
				if (pinsDowned.size == 5 && !Frame.isLastFrame(frameIndex)) {
					break
				}
			}

			if (
				frameIndex < lastValidFrameIndex &&
				frameRolls.size < Frame.NUMBER_OF_ROLLS &&
				!pinsDowned.arePinsCleared()
			) {
				// Insert empty rolls for any unrecorded
				cleanFrameRolls.addAll(
					Frame.rollIndicesAfter(frameRolls.lastIndex).map {
						ScoreKeeperInput.Roll(frameIndex, rollIndex = it, pinsDowned = setOf(), didFoul = false)
					},
				)
			}

			cleanRolls.add(cleanFrameRolls)
		}

		return ScoreKeeperState(
			input = ScoreKeeperInput(cleanRolls),
			lastValidFrameIndex = lastValidFrameIndex,
			accruingScore = 0,
		)
	}

	private fun getRollsForFrame(
		rolls: List<List<ScoreKeeperInput.Roll>>,
		frameIndex: Int,
		atListIndex: Int,
	): List<ScoreKeeperInput.Roll>? {
		if (atListIndex >= rolls.size) return null
		return if (rolls[atListIndex].firstOrNull()?.frameIndex == frameIndex) {
			rolls[atListIndex]
		} else {
			null
		}
	}

	private fun getSubsequentRolls(
		nextFrameRolls: List<ScoreKeeperInput.Roll>?,
		nextNextFrameRolls: List<ScoreKeeperInput.Roll>?,
	): Pair<ScoreKeeperInput.Roll?, ScoreKeeperInput.Roll?> {
		val nextRoll = nextFrameRolls?.firstOrNull() ?: return Pair(null, null)
		return if (nextRoll.pinsDowned.arePinsCleared()) {
			if (Frame.isLastFrame(nextRoll.frameIndex)) {
				Pair(nextRoll, if (nextFrameRolls.size >= 2) nextFrameRolls[1] else null)
			} else {
				Pair(nextRoll, nextNextFrameRolls?.firstOrNull())
			}
		} else {
			Pair(nextRoll, if (nextFrameRolls.size >= 2) nextFrameRolls[1] else null)
		}
	}

	private fun padRolls(rolls: MutableList<ScoringRoll>, display: String?) {
		rolls.addAll(
			Frame.rollIndicesAfter(rolls.lastIndex).map {
				ScoringRoll(index = it, display = display, didFoul = false, isSecondaryValue = false)
			},
		)
	}

	private fun padFrames(frames: MutableList<ScoringFrame>) {
		frames.addAll(
			Game.frameIndicesAfter(frames.lastIndex).map {
				val rolls = mutableListOf<ScoringRoll>()
				padRolls(rolls, display = null)
				ScoringFrame(index = it, rolls = rolls, score = null)
			},
		)
	}
}

internal fun ScoreKeeperState.applyPenalty(applied: Boolean) {
	accruingScore -= (if (applied) Game.FOUL_PENALTY else 0)
}

internal fun ScoreKeeperState.displayScoreForFrame(frameIndex: Int): Int? =
	if (frameIndex <= lastValidFrameIndex) maxOf(accruingScore, 0) else null
