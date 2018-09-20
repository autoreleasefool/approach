package ca.josephroque.bowlingcompanion.utils

import android.content.Context
import android.os.Build
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import ca.josephroque.bowlingcompanion.R
import android.widget.TextView
import android.text.Html
import android.view.View

/**
 * Copyright (C) 2018 Joseph Roque
 *
 *  Provides utilities to handle displaying the changelog of the application to the user
 *  each time a new update becomes available.
 */
object Changelog {

    fun show(context: Context) {
        // FIXME: try to use ScrollableTextDialog instead of this custom alert dialog
        val changelogText = Files.retrieveTextFileAsset(context, "changelog.txt") ?: return

        val dialog = AlertDialog.Builder(context)
        val rootView = View.inflate(context, R.layout.dialog_scrollable_text, null)

        dialog.setView(rootView)
        val alertDialog = dialog.create()

        // Setting text of changelog based on Android SDK version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            rootView.findViewById<TextView>(R.id.tv_scrollable).text = Html.fromHtml(changelogText.replace("\n", "<br />"), 0)
        } else {
            rootView.findViewById<TextView>(R.id.tv_scrollable).text = Html.fromHtml(changelogText.replace("\n", "<br />"))
        }

        rootView.findViewById<Toolbar>(R.id.toolbar_scrollable).apply {
            setTitle(R.string.changelog)
            setNavigationIcon(R.drawable.ic_dismiss)
            setNavigationOnClickListener {
                alertDialog.dismiss()
            }
        }

        alertDialog.show()
    }
}
