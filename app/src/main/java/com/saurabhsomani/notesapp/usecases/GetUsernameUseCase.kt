package com.saurabhsomani.notesapp.usecases

import android.content.Context
import com.google.firebase.auth.FirebaseUser
import com.saurabhsomani.notesapp.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetUsernameUseCase @Inject constructor(
    private val firebaseUser: FirebaseUser?,
    @ApplicationContext private val context: Context
) {

    operator fun invoke(): String {
        val username = firebaseUser?.let {
            if (it.isAnonymous) {
                context.getString(R.string.username_guest)
            } else if (!it.displayName.isNullOrEmpty()) {
                it.displayName
            } else if (!it.phoneNumber.isNullOrEmpty()) {
                it.phoneNumber
            } else {
                ""
            }
        } ?: ""

        return context.getString(R.string.notes_screen_username_text, username)
    }

}