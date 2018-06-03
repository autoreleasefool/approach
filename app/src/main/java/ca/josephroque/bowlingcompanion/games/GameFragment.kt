package ca.josephroque.bowlingcompanion.games

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.fragments.BaseFragment
import ca.josephroque.bowlingcompanion.common.interfaces.IFloatingActionButtonHandler
import ca.josephroque.bowlingcompanion.games.views.FrameView
import ca.josephroque.bowlingcompanion.games.views.GameFooterView
import ca.josephroque.bowlingcompanion.games.views.GameHeaderView
import ca.josephroque.bowlingcompanion.games.views.PinLayout
import ca.josephroque.bowlingcompanion.matchplay.MatchPlayResult
import ca.josephroque.bowlingcompanion.matchplay.MatchPlaySheet
import ca.josephroque.bowlingcompanion.series.Series
import kotlinx.android.synthetic.main.fragment_game.*
import kotlinx.android.synthetic.main.sheet_match_play.*
import kotlinx.android.synthetic.main.sheet_match_play.view.*

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Display game details and allow the user to edit.
 */
class GameFragment : BaseFragment(),
        IFloatingActionButtonHandler,
        FrameView.FrameInteractionDelegate,
        PinLayout.PinLayoutInteractionDelegate,
        GameFooterView.GameFooterInteractionDelegate,
        GameHeaderView.GameHeaderInteractionDelegate,
        MatchPlaySheet.MatchPlaySheetDelegate {

    /** IDs for frame views. */
    private val frameViewIds = intArrayOf(R.id.frame_0, R.id.frame_1, R.id.frame_2, R.id.frame_3,
            R.id.frame_4, R.id.frame_5, R.id.frame_6, R.id.frame_7, R.id.frame_8, R.id.frame_9)

    /** Frame view instances. */
    private lateinit var frameViews: Array<FrameView?>

    /** The number of the current game in its series. */
    var gameNumber: Int = 0
        set(value) {
            field = value
            game_header.currentGame = gameNumber
        }
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

    /** The series being edited. */
    private var series: Series? = null

    /** @Override */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        series = savedInstanceState?.getParcelable(ARG_SERIES) ?: arguments?.getParcelable(ARG_SERIES)
        val view = inflater.inflate(R.layout.fragment_game, container, false)

        frameViews = arrayOfNulls(frameViewIds.size)
        frameViewIds.forEachIndexed { index, it ->
            frameViews[index] = view.findViewById(it)
        }

        setupBottomSheet(view)

        return view
    }

    /** @Override */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(ARG_SERIES, series)
    }

    /** @Override */
    override fun onStart() {
        super.onStart()

        frameViews.forEach {
            it?.delegate = this
        }

        pin_layout.delegate = this
        game_footer.delegate = this
        game_header.delegate = this
    }

    private fun setupBottomSheet(rootView: View) {
        val bottomSheet = rootView.sheet_match_play
        val sheetBehavior = BottomSheetBehavior.from(bottomSheet)
        sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    /** @Override */
    override fun getFabImage(): Int? {
        return R.drawable.ic_arrow_forward
    }

    /** @Override */
    override fun onFabClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    // MARK: FrameInteractionDelegate

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

    // MARK: PinLayoutInteractionDelegate

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

    // MARK: GameFooterInteractionDelegate

    /** @Override */
    override fun onClearPins() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /** @Override */
    override fun onFoulToggle() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /** @Override */
    override fun onLockToggle() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /** @Override */
    override fun onMatchPlaySettings() {
        val fragment = MatchPlaySheet.newInstance()
        fragmentNavigation?.showBottomSheet(fragment, MatchPlaySheet.FRAGMENT_TAG)
    }

    // MARK: GameHeaderInteractionDelegate

    /** @Override */
    override fun onNextBall() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /** @Override */
    override fun onPrevBall() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    // MARK: MatchPlaySheetDelegate

    /** @Override */
    override fun onFinishedSettingMatchPlayResults(
            opponentName: String,
            opponentScore: Int,
            matchPlayResult: MatchPlayResult,
            inputValid: Boolean
    ) {
        if (!inputValid) {
            val sheetBehavior = BottomSheetBehavior.from(sheet_match_play)
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            return
        }

        // TODO: set match play values
    }

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "GameFragment"

        /** Argument identifier for passing a [Series] to this fragment. */
        private const val ARG_SERIES = "${TAG}_series"

        /**
         * Creates a new instance.
         *
         * @param series the series to edit games for
         * @return the new instance
         */
        fun newInstance(series: Series): GameFragment {
            val fragment = GameFragment()
            val args = Bundle()
            args.putParcelable(ARG_SERIES, series)
            fragment.arguments = args
            return fragment
        }
    }

    /**
     * Handle interactions with the game fragment.
     */
    interface OnGameFragmentInteractionListener {
        fun enableFab(enabled: Boolean)
    }
}
