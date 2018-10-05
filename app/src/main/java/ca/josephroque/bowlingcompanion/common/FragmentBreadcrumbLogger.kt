package ca.josephroque.bowlingcompanion.common

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.bugsnag.android.BreadcrumbType
import com.bugsnag.android.Bugsnag

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Add breadcrumb logging to fragments for Bugsnag.
 */
class FragmentBreadcrumbLogger : FragmentManager.FragmentLifecycleCallbacks() {
    companion object {
        @Suppress("unused")
        private const val TAG = "FragmentBreadcrumbLogger"

        private const val FRAG_LIFECYCLE_CALLBACK = "FragmentLifecycleCallback"
    }

    // MARK: FragmentLifecycleCallbacks

    override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
        leaveLifecycleBreadcrumb(f, "onFragmentCreated()")
    }

    override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
        leaveLifecycleBreadcrumb(f, "onFragmentResumed()")
    }

    override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
        leaveLifecycleBreadcrumb(f, "onFragmentPaused()")
    }

    // MARK: Private functions

    private fun leaveLifecycleBreadcrumb(fragment: Fragment, lifecycleCallback: String) {
        val fragmentName = fragment.javaClass.simpleName

        val metadata = HashMap<String, String>()
        metadata[FRAG_LIFECYCLE_CALLBACK] = lifecycleCallback
        Bugsnag.leaveBreadcrumb(fragmentName, BreadcrumbType.NAVIGATION, metadata)
    }
}
