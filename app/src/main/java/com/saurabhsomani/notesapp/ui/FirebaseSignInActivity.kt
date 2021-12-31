package com.saurabhsomani.notesapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.saurabhsomani.notesapp.R
import com.saurabhsomani.notesapp.databinding.ActivityFirebaseSignInBinding

class FirebaseSignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFirebaseSignInBinding
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_firebase_sign_in)

        if (FirebaseAuth.getInstance().currentUser != null) {
            navigateToMainActivity()
        }

        binding.loginBtn.setOnClickListener {
            Log.d(TAG, "loginBtn clicked: ")
            createAndLaunchSignInIntent()
        }
    }

    private fun createAndLaunchSignInIntent() {
        // Choose authentication providers
        val providers = listOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        // Create sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setTheme(R.style.Theme_NotesApp)
            .build()

        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        Log.d(TAG, "onSignInResult: ${result.resultCode}")
        Log.d(TAG, "onSignInResult: $response")
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            // ...
            updateUI(user)
        } else {
            // Sign in failed
            if (response == null) {
                // User pressed back button
                showSnackbar(R.string.sign_in_cancelled)
                return
            }

            if (response.error?.errorCode == ErrorCodes.NO_NETWORK) {
                showSnackbar(R.string.no_internet_connection)
                return
            }

            showSnackbar(R.string.unknown_error)
        }
    }

    private fun showSnackbar(messageResId: Int) {
        Snackbar.make(binding.root, messageResId, Snackbar.LENGTH_SHORT).show()
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        Log.d(TAG, "updateUI: $currentUser")
        currentUser?.let {
            navigateToMainActivity()
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    //    private fun signOut() {
//        // [START auth_fui_signout]
//        AuthUI.getInstance()
//            .signOut(this)
//            .addOnCompleteListener {
//                // ...
//            }
//        // [END auth_fui_signout]
//    }

//    private fun delete() {
//        // [START auth_fui_delete]
//        AuthUI.getInstance()
//            .delete(this)
//            .addOnCompleteListener {
//                // ...
//            }
//        // [END auth_fui_delete]
//    }

//    private fun themeAndLogo() {
//        val providers = emptyList<AuthUI.IdpConfig>()
//
//        // [START auth_fui_theme_logo]
//        val signInIntent = AuthUI.getInstance()
//            .createSignInIntentBuilder()
//            .setAvailableProviders(providers)
//            .setLogo(R.drawable.my_great_logo) // Set logo drawable
//            .setTheme(R.style.MySuperAppTheme) // Set theme
//            .build()
//        signInLauncher.launch(signInIntent)
//        // [END auth_fui_theme_logo]
//    }

//    private fun privacyAndTerms() {
//        val providers = emptyList<AuthUI.IdpConfig>()
//        // [START auth_fui_pp_tos]
//        val signInIntent = AuthUI.getInstance()
//            .createSignInIntentBuilder()
//            .setAvailableProviders(providers)
//            .setTosAndPrivacyPolicyUrls(
//                "https://example.com/terms.html",
//                "https://example.com/privacy.html")
//            .build()
//        signInLauncher.launch(signInIntent)
//        // [END auth_fui_pp_tos]
//    }

//    open fun emailLink() {
//        // [START auth_fui_email_link]
//        val actionCodeSettings = ActionCodeSettings.newBuilder()
//            .setAndroidPackageName( /* yourPackageName= */
//                "...",  /* installIfNotAvailable= */
//                true,  /* minimumVersion= */
//                null)
//            .setHandleCodeInApp(true) // This must be set to true
//            .setUrl("https://google.com") // This URL needs to be whitelisted
//            .build()
//
//        val providers = listOf(
//            AuthUI.IdpConfig.EmailBuilder()
//                .enableEmailLinkSignIn()
//                .setActionCodeSettings(actionCodeSettings)
//                .build()
//        )
//        val signInIntent = AuthUI.getInstance()
//            .createSignInIntentBuilder()
//            .setAvailableProviders(providers)
//            .build()
//        signInLauncher.launch(signInIntent)
//        // [END auth_fui_email_link]
//    }
//
//    open fun catchEmailLink() {
//        val providers: List<AuthUI.IdpConfig> = emptyList()
//
//        // [START auth_fui_email_link_catch]
//        if (AuthUI.canHandleIntent(intent)) {
//            val extras = intent.extras ?: return
//            val link = extras.getString(ExtraConstants.EMAIL_LINK_SIGN_IN)
//            if (link != null) {
//                val signInIntent = AuthUI.getInstance()
//                    .createSignInIntentBuilder()
//                    .setEmailLink(link)
//                    .setAvailableProviders(providers)
//                    .build()
//                signInLauncher.launch(signInIntent)
//            }
//        }
//        // [END auth_fui_email_link_catch]
//    }

    companion object {
        private const val TAG = "FirebaseSignInActivity"
    }
}