package com.luccas.passelivredocumentos.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log


import com.google.firebase.auth.FirebaseAuth
import com.luccas.passelivredocumentos.ui.MainActivity
import kotlinx.android.synthetic.main.activity_auth.*
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.firestore.FirebaseFirestore
import com.luccas.passelivredocumentos.R
import android.widget.Toast
import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.luccas.passelivredocumentos.databinding.AuthBinding
import com.luccas.passelivredocumentos.models.User
import com.luccas.passelivredocumentos.ui.base.BaseActivity
import com.luccas.passelivredocumentos.utils.openActivity


@Suppress("DEPRECATED_IDENTITY_EQUALS")
class AuthActivity : BaseActivity<AuthViewModel>() {

    private lateinit var firebaseAuth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    val RC_SIGN_IN: Int = 1
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions

    companion object {
        val TAG = "LOG: "
    }

    override val layoutRes = R.layout.activity_auth
    override fun getViewModel() = AuthViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        configureGoogleSignIn()
        sign_in_button.setOnClickListener {
            signIn()
        }
    }
    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    private fun configureGoogleSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }


    override fun onResume() {
        super.onResume()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            openActivity<MainActivity> (
                finishWhenOpen = true,
                enterAnim = R.anim.slide_from_right,
                exitAnim = R.anim.slide_to_left
            ){
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                firebaseAuth = FirebaseAuth.getInstance()
                insertNewUserIntoDatabase()
            } else {
                Toast.makeText(this, "Login com google falhou, tente novamente mais tarde!", Toast.LENGTH_LONG).show()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {

                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        }
    }

    // insere usuario no firebase
    private fun insertNewUserIntoDatabase() {
        val userData =
            User(
                firebaseAuth.currentUser!!.uid,
                firebaseAuth.currentUser!!.displayName!!, firebaseAuth.currentUser!!.email!!,firebaseAuth.currentUser!!.photoUrl.toString()
            )

        val ref = db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
        ref.set(userData)
            .addOnSuccessListener {
                Log.i(
                    "Success",
                    userData.id+userData.name
                )
                sharedPref.edit().putString("userID",userData.id).apply()
                sharedPref.edit().putString("userEmail",userData.email).apply()
                sharedPref.edit().putString("userName",userData.name).apply()
                sharedPref.edit().putString("photoUrl",userData.photoUrl).apply()

                openActivity<MainActivity>(
                    finishWhenOpen = true,
                    enterAnim =  R.anim.nav_default_pop_enter_anim,
                    exitAnim =  R.anim.nav_default_pop_exit_anim
                ) {  }
            }
            .addOnFailureListener { Log.e("Error", userData.id) }
    }

}
