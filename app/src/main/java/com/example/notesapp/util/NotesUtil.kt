package com.example.notesapp.util

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

fun formatNoteDate(timestamp: Long): String {
    return SimpleDateFormat("EEE, d MMM yyyy hh:mm:ss a", Locale.getDefault())
        .format(timestamp)
}

fun Context?.getResStrByName(resName: String): String {
    return this?.let {
        val resId = resources.getIdentifier(resName, "string", packageName)
        if (resId == 0) {
            ""
        } else {
            getString(resId)
        }
    } ?: ""
}