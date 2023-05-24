package ca.josephroque.bowlingcompanion.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.fragments.BaseFragment
import kotlinx.android.synthetic.main.fragment_tutorial.iv_tutorial as ivTutorial
import kotlinx.android.synthetic.main.fragment_tutorial.tv_tutorial as tvTutorial

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Present an instructional message to the user.
 */
class TutorialFragment : BaseFragment() {

    companion object {
        @Suppress("unused")
        private const val TAG = "TutorialFragment"

        private const val ARG_TUTORIAL_ITEM = "tutorial_item"

        enum class TutorialItem(val title: Int, val image: Int) {
            Welcome(R.string.tutorial_welcome, R.drawable.pin_enabled),
            Bowlers(R.string.tutorial_bowlers, R.drawable.tutorial_bowlers),
            Recording(R.string.tutorial_recording, R.drawable.tutorial_recording),
            Statistics(R.string.tutorial_statistics, R.drawable.tutorial_statistics);

            companion object {
                private val map = TutorialItem.values().associateBy(TutorialItem::ordinal)
                fun fromInt(type: Int) = map[type]
            }
        }

        fun newInstance(page: Int): TutorialFragment {
            val fragment = TutorialFragment()
            fragment.arguments = Bundle().apply { putInt(ARG_TUTORIAL_ITEM, page) }
            return fragment
        }
    }

    private lateinit var tutorialItem: TutorialItem

    // MARK: Lifecycle functions

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        tutorialItem = TutorialItem.fromInt(arguments?.getInt(ARG_TUTORIAL_ITEM)!!)!!
        return inflater.inflate(R.layout.fragment_tutorial, container, false)
    }

    override fun onStart() {
        super.onStart()
        tvTutorial.setText(tutorialItem.title)
        ivTutorial.setImageResource(tutorialItem.image)
    }

    // MARK: BaseFragment

    override fun updateToolbarTitle() {
        // Intentionally left blank
    }
}
