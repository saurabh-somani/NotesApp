package com.saurabhsomani.notesapp.network

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.saurabhsomani.notesapp.database.entities.Note
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class NetworkUseCase @Inject constructor(
    private val firestoreDb: FirebaseFirestore
) {

    suspend fun uploadNotes(notes: List<Note>) {
        val networkNotesList = notes.map { it.toNetworkNote() }
        val networkNotes = NetworkNotes(networkNotesList)
        uploadNetworkNotes(networkNotes)
    }

    private suspend fun uploadNetworkNotes(networkNotes: NetworkNotes) =
        suspendCancellableCoroutine<Unit> { continuation ->
            firestoreDb.collection("notes").document("notesList")
                .set(networkNotes)
                .addOnSuccessListener {
                    Log.d(TAG, "uploadData: success")
                    continuation.resume(Unit)
                }.addOnFailureListener {
                    Log.e(TAG, "uploadData: failure", it)
                    continuation.resume(Unit)
                }
        }

    suspend fun downloadNotes(): List<Note> {
        return downloadNetworkNotes().map { it.toNote() }
    }

    private suspend fun downloadNetworkNotes(): List<NetworkNote> =
        suspendCancellableCoroutine { continuation ->
            firestoreDb.collection("notes").document("notesList")
                .get()
                .addOnSuccessListener { document ->
                    val networkNotes = document?.toObject(NetworkNotes::class.java)
                    continuation.resume(networkNotes?.networkNotesList ?: emptyList())
                }.addOnFailureListener {
                    continuation.resume(emptyList())
                }
        }

    companion object {
        private const val TAG = "NetworkUseCase"
    }
}