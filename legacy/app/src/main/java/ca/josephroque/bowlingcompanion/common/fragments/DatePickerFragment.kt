package ca.josephroque.bowlingcompanion.common.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import java.util.Calendar

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Pick a date.
 */
class DatePickerFragment : BaseDialogFragment(),
        DatePickerDialog.OnDateSetListener {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "DatePickerDialog"

        /** Year to show in the calendar. */
        private const val ARG_YEAR = "${TAG}_year"

        /** Month to show in the calendar. */
        private const val ARG_MONTH = "${TAG}_month"

        /** Day to show in the calendar. */
        private const val ARG_DAY = "${TAG}_day"

        fun newInstance(calendar: Calendar): DatePickerFragment {
            val fragment = DatePickerFragment()
            fragment.arguments = Bundle().apply {
                putInt(ARG_DAY, calendar.get(Calendar.DAY_OF_MONTH))
                putInt(ARG_MONTH, calendar.get(Calendar.MONTH))
                putInt(ARG_YEAR, calendar.get(Calendar.YEAR))
            }
            return fragment
        }
    }

    /** Listener for DatePickerDialog events. */
    var listener: DatePickerDialog.OnDateSetListener? = null

    /** @Override */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        var day = calendar.get(Calendar.DAY_OF_MONTH)

        arguments?.let {
            year = it.getInt(ARG_YEAR, year)
            month = it.getInt(ARG_MONTH, month)
            day = it.getInt(ARG_DAY, day)
        }

        return DatePickerDialog(activity!!, this, year, month, day)
    }

    /** @Override */
    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /** @Override */
    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        listener?.onDateSet(view, year, month, day)
    }
}
