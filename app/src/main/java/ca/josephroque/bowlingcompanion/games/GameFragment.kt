package ca.josephroque.bowlingcompanion.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.fragments.BaseFragment
import ca.josephroque.bowlingcompanion.common.interfaces.IFloatingActionButtonHandler
import ca.josephroque.bowlingcompanion.games.views.FrameView
import ca.josephroque.bowlingcompanion.games.views.PinLayout
import kotlinx.android.synthetic.main.fragment_game.view.*

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Display game details and allow the user to edit.
 */
class GameFragment : BaseFragment(),
        IFloatingActionButtonHandler,
        FrameView.FrameInteractionDelegate,
        PinLayout.PinLayoutInteractionDelegate {

    /** IDs for frame views. */
    private val frameViewIds = intArrayOf(R.id.frame_1, R.id.frame_2, R.id.frame_3, R.id.frame_4,
            R.id.frame_5, R.id.frame_6, R.id.frame_7, R.id.frame_8, R.id.frame_9, R.id.frame_10)

    /** Frame view instances. */
    private lateinit var frameViews: Array<FrameView?>

    /** The current frame being edited. */
    private var currentFrame: Int = 0
    /** The current ball being edited. */
    private var currentBall: Int = 0

    /** The current pins up (false) and knocked down (true) for each frame, ball, and pin. */
    @Suppress("LABEL_NAME_CLASH")
    private var pinState: Array<Array<BooleanArray>> = Array(Game.NUMBER_OF_FRAMES, {
        return@Array Array(Frame.NUMBER_OF_BALLS, {
            return@Array BooleanArray(Game.NUMBER_OF_PINS)
        })
    })

    /** @Override */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_game, container, false)

        frameViews = arrayOfNulls(frameViewIds.size)
        frameViewIds.forEachIndexed { index, it ->
            frameViews[index] = view.findViewById(it)
        }

        view.pin_layout.delegate = this

        return view
    }

    override fun onStart() {
        super.onStart()

        frameViews.forEach {
            it?.delegate = this
        }
    }

    /** @Override */
    override fun getFabImage(): Int? {
        return R.drawable.ic_arrow_forward
    }

    /** @Override */
    override fun onFabClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /** @Override */
    override fun onBallSelected(ball: Int, frame: Int) {
        currentBall = ball
        currentFrame = frame

        frameViews.forEachIndexed { index, it ->
            it?.isCurrentFrame = (index == currentFrame)
            it?.currentBall = currentBall
        }
    }

    /** @Override */
    override fun onFrameSelected(frame: Int) {
        onBallSelected(0, frame)
    }

    /** @Override */
    override fun getPinState(pin: Int): Boolean {
        return pinState[currentFrame][currentBall][pin]
    }

    /** @Override */
    override fun updatePinState(pins: IntArray, state: Boolean) {
        pins.forEach {
            pinState[currentFrame][currentBall][it] = state
        }
    }

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "GameFragment"

        /**
         * Creates a new instance.
         *
         * @return the new instance
         */
        fun newInstance(): GameFragment {
            return GameFragment()
        }
    }
}
