package com.example.notesapp.util

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

suspend inline fun <T> Flow<T>.collectFlowWithLifecycleStarted(
    lifecycle: Lifecycle,
    crossinline action: suspend (value: T) -> Unit
) = flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
    .collect(action)