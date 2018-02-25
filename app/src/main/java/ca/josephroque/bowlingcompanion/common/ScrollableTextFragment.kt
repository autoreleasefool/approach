package ca.josephroque.bowlingcompanion.common


import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import kotlinx.android.synthetic.main.fragment_scrollable_text.*

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Presents scrollable text.
 */
class ScrollableTextFragment : Fragment() {

    companion object {
        /** Logging identifier. */
        private const val TAG = "ScrollableTextFragment"

        /** Argument identifier for title of fragment. */
        private const val TITLE = "${TAG}_TITLE"

        /** Argument identifier for text to display. */
        private const val TEXT = "${TAG}_TEXT"

        /**
         * Create a new instance of [ScrollableTextFragment].
         *
         * @param text the text to display
         * @return the new instance
         */
        fun newInstance(@StringRes title: Int, text: CharSequence): ScrollableTextFragment {
            val fragment = ScrollableTextFragment()
            val args = Bundle().apply {
                putInt(TITLE, title)
                putCharSequence(TEXT, text)
            }

            fragment.arguments = args
            return fragment
        }
    }

    /** Title of the fragment. */
    private var title: Int = 0

    /** String to display. */
    private var text: CharSequence? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        savedInstanceState?.let {
            title = it.getInt(TITLE)
            text = it.getCharSequence(TEXT)
        } ?: run {
            arguments?.let {
                title = it.getInt(TITLE)
                text = it.getCharSequence(TEXT)
            }
        }

        return inflater.inflate(R.layout.fragment_scrollable_text, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
            putInt(TITLE, title)
            putCharSequence(TEXT, text)
        }
    }

    override fun onResume() {
        super.onResume()

        toolbar_scrollable.setTitle(title)
        tv_scrollable.text = text
    }

}
