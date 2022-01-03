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
        uploadNetworkNotes(networkNotesList)
    }

    private suspend fun uploadNetworkNotes(networkNotes: List<NetworkNote>) =
        suspendCancellableCoroutine<Unit> { continuation ->
            val batch = firestoreDb.batch()
            networkNotes.forEach {
                val docRef = firestoreDb.collection("notes")
                    .document(it.id.toString())
                batch.set(docRef, it)
            }
            batch.commit().addOnCompleteListener {
                Log.d(TAG, "uploadNetworkNotes: ")
                continuation.resume(Unit)
            }
        }

    suspend fun downloadNotes(): List<Note> {
        return downloadNetworkNotes().map { it.toNote() }
    }

    private suspend fun downloadNetworkNotes(): List<NetworkNote> =
        suspendCancellableCoroutine { continuation ->
            firestoreDb.collection("notes").get().addOnSuccessListener { result ->
                val networkNotes = result.documents.mapNotNull { snapshot ->
                    snapshot.toObject(NetworkNote::class.java).also {
                        it?.id = snapshot.id.toLongOrNull() ?: 0
                    }
                }
                continuation.resume(networkNotes)
            }.addOnFailureListener {
                continuation.resume(emptyList())
            }
        }

    companion object {
        private const val TAG = "NetworkUseCase"
    }
}