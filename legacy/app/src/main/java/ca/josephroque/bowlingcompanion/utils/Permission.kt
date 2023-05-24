package ca.josephroque.bowlingcompanion.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import ca.josephroque.bowlingcompanion.R

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Permission the app requests
 */
enum class Permission {
    WriteExternalStorage;

    companion object {
        fun fromRequestCode(requestCode: Int): Permission {
            return Permission.values().first { it.requestCode == requestCode }
        }
    }

    private val manifestPermission: String
        get() {
            return when (this) {
                WriteExternalStorage -> Manifest.permission.WRITE_EXTERNAL_STORAGE
            }
        }

    val requestCode: Int = ordinal

    private val explanationTitle: Int
        get() {
            return when (this) {
                WriteExternalStorage -> R.string.write_external_storage_permission_title
            }
        }

    private val explanationMessage: Int
        get() {
            return when (this) {
                WriteExternalStorage -> R.string.write_external_storage_permission_message
            }
        }

    fun requestPermission(activity: Activity): Boolean {
        if (ContextCompat.checkSelfPermission(activity, manifestPermission) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, manifestPermission)) {
                // Show an explanation and request the permission again
                AlertDialog.Builder(activity)
                        .setTitle(explanationTitle)
                        .setMessage(explanationMessage)
                        .setPositiveButton(R.string.okay, null)
                        .create()
                        .show()
            } else {
                // No explanation required, request the permission
                ActivityCompat.requestPermissions(activity, arrayOf(manifestPermission), requestCode)
            }

            return false
        }

        // Permission has already been granted
        return true
    }
}
