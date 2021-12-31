package com.example.notesapp.util

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import java.text.SimpleDateFormat
import java.util.*

fun formatNoteDate(timestamp: Long): String {
    return SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
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

suspend inline fun <T> Flow<T>.collectFlowWithLifecycleStarted(
    lifecycle: Lifecycle,
    crossinline action: suspend (value: T) -> Unit
) = flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
    .collect(action)