package ca.josephroque.bowlingcompanion.games.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.games.Game
import ca.josephroque.bowlingcompanion.settings.Settings
import kotlinx.android.synthetic.main.view_score_sheet.view.tv_final_score as tvFinalScore

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Display a detailed view of the score of a game.
 */
class ScoreSheet : HorizontalScrollView {

    companion object {
        @Suppress("unused")
        private const val TAG = "ScoreSheet"
    }

    private val frameViewIds = intArrayOf(R.id.frame_0, R.id.frame_1, R.id.frame_2, R.id.frame_3,
            R.id.frame_4, R.id.frame_5, R.id.frame_6, R.id.frame_7, R.id.frame_8, R.id.frame_9)

    private var frameViews: Array<FrameView?>

    var delegate: SheetScrollListener? = null

    var frameViewDelegate: FrameView.FrameInteractionDelegate? = null
        set(value) {
            field = value
            frameViews.forEach {
                it?.delegate = value
            }
        }

    var shouldHighlightMarks: Boolean = Settings.BooleanSetting.EnableStrikeHighlights.default
        set(value) {
            field = value
            frameViews.forEach {
                it?.shouldHighlightMarks = value
            }
        }

    var frameNumbersEnabled: Boolean = true
        set(value) {
            field = value
            frameViews.forEach { it?.frameNumberVisible = value }

            val layoutParams = tvFinalScore.layoutParams as? LinearLayout.LayoutParams ?: return
            layoutParams.bottomMargin = if (value) {
                context.resources.getDimensionPixelSize(R.dimen.frame_number_height)
            } else {
                0
            }
            tvFinalScore.layoutParams = layoutParams
        }

    var finalScore: Int = 0
        set(value) {
            field = value
            tvFinalScore.text = value.toString()
        }

    // MARK: Constructors

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.view_score_sheet, this, true)

        frameViews = arrayOfNulls(frameViewIds.size)
        frameViewIds.forEachIndexed { index, it ->
            frameViews[index] = findViewById(it)
        }
    }

    // MARK: Lifecycle functions

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        frameViewDelegate = null
        delegate = null
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        delegate?.didScroll(l, t)

    }

    // MARK: ScoreSheet

    fun apply(currentFrameIdx: Int, currentBallIdx: Int, game: Game) {
        // Set final score of the game
        finalScore = game.score

        // Update frames with marks and pins
        val scores = game.getScoreTextForFrames()
        val balls = game.getBallTextForFrames()
        updateFrames(currentFrameIdx, currentBallIdx, scores, balls)

        // Update fouls
        game.frames.forEachIndexed { frameIdx, frame ->
            frame.ballFouled.forEachIndexed { ballIdx, foul ->
                setFoulEnabled(frameIdx, ballIdx, foul)
            }
        }
    }

    fun updateFrames(currentFrameIdx: Int, currentBallIdx: Int, scores: List<String>, balls: List<Array<String>>) {
        frameViews.forEachIndexed { frameIdx, it ->
            it?.isCurrentFrame = (frameIdx == currentFrameIdx)
            it?.currentBall = currentBallIdx
            it?.setFrameText(scores[frameIdx])
            balls[frameIdx].forEachIndexed { ballIdx, ball ->
                it?.setBallText(ballIdx, ball)
            }
        }
    }

    fun setFoulEnabled(frameIdx: Int, ballIdx: Int, enabled: Boolean) {
        frameViews[frameIdx]?.setFoulEnabled(ballIdx, enabled)
    }

    fun focusOnFrame(isFirstFocus: Boolean, isLastFrame: Boolean, frameIdx: Int) {
        val left = if (frameIdx >= 1 && !(isFirstFocus && isLastFrame)) {
            val prevFrame = frameViews[frameIdx - 1] ?: return
            prevFrame.left
        } else {
            val frame = frameViews[frameIdx] ?: return
            frame.left
        }

        post { smoothScrollTo(left, 0) }
    }

    interface SheetScrollListener {
        fun didScroll(x: Int, y: Int)
    }
}
