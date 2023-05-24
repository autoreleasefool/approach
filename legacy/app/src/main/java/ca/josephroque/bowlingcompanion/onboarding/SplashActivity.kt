package ca.josephroque.bowlingcompanion.onboarding

import ca.josephroque.bowlingcompanion.common.activities.BaseActivity
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import ca.josephroque.bowlingcompanion.NavigationActivity
import ca.josephroque.bowlingcompanion.R
import kotlinx.android.synthetic.main.activity_splash.tv_next as tvNext
import kotlinx.android.synthetic.main.activity_splash.tv_prev as tvPrev
import kotlinx.android.synthetic.main.activity_splash.tv_skip as tvSkip
import kotlinx.android.synthetic.main.activity_splash.view_pager as viewPager

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Activity to introduce the user to the app.
 */
class SplashActivity : BaseActivity() {

    companion object {
        @Suppress("unused")
        private const val TAG = "SplashActivity"

        private const val TUTORIAL_WATCHED = "tutorial_watched"
    }

    private var tutorialWatched: Boolean
        get() = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(TUTORIAL_WATCHED, false)
        set(value) = PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(TUTORIAL_WATCHED, value).apply()

    private val onClickListener = View.OnClickListener {
        when (it.id) {
            R.id.tv_next -> {
                if (viewPager.currentItem < TutorialFragment.Companion.TutorialItem.values().lastIndex) {
                    viewPager.currentItem += 1
                } else {
                    openMainActivity()
                }
            }
            R.id.tv_prev -> {
                if (viewPager.currentItem > 0) {
                    viewPager.currentItem -= 1
                }
            }
            R.id.tv_skip -> {
                openMainActivity()
            }
        }

        updateToolbar()
    }

    private val onPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
            // Intentionally left blank
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            // Intentionally left blank
        }

        override fun onPageSelected(position: Int) {
            updateToolbar()
        }
    }

    // MARK: Lifecycle functions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (tutorialWatched) {
            openMainActivity()
            return
        }

        tutorialWatched = true
        setupViewPager()
        setupNavigation()
        updateToolbar()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewPager.removeOnPageChangeListener(onPageChangeListener)
    }

    override fun onResume() {
        super.onResume()
        updateToolbar()
    }

    // MARK: Private functions

    private fun updateToolbar() {
        tvPrev.visibility = if (viewPager.currentItem > 0) {
            View.VISIBLE
        } else {
            View.GONE
        }

        tvNext.setText(if (viewPager.currentItem < TutorialFragment.Companion.TutorialItem.values().lastIndex) {
            R.string.next
        } else {
            R.string.done
        })
    }

    private fun setupViewPager() {
        val adapter = SplashPagerAdapter(supportFragmentManager)
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(onPageChangeListener)
    }

    private fun setupNavigation() {
        tvPrev.setOnClickListener(onClickListener)
        tvNext.setOnClickListener(onClickListener)
        tvSkip.setOnClickListener(onClickListener)
    }

    private fun openMainActivity() {
        val mainActivityIntent = Intent(this, NavigationActivity::class.java)
        startActivity(mainActivityIntent)
        finish()
    }

    // MARK: SplashPagerAdapter

    class SplashPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return TutorialFragment.newInstance(position)
        }

        override fun getCount(): Int {
            return TutorialFragment.Companion.TutorialItem.values().size
        }
    }
}
