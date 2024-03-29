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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.iid.FirebaseInstanceId
import com.luccas.passelivredocumentos.R.string
import com.luccas.passelivredocumentos.TermsAndPoliticsActivity
import com.luccas.passelivredocumentos.models.User
import com.luccas.passelivredocumentos.ui.base.BaseActivity
import com.luccas.passelivredocumentos.utils.Common
import com.luccas.passelivredocumentos.utils.Common.Companion.email
import com.luccas.passelivredocumentos.utils.Common.Companion.fullname
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
            showBottomSheetProgress()
            signIn()
        }
        tv_politics_click.setOnClickListener {
            openActivity<TermsAndPoliticsActivity>(
                enterAnim = R.anim.slide_from_right,
                exitAnim = R.anim.slide_to_left
            ) { putExtra("type", Common.politics)}
        }
        tv_terms_click.setOnClickListener {
            openActivity<TermsAndPoliticsActivity>(
                enterAnim = R.anim.slide_from_right,
                exitAnim = R.anim.slide_to_left
            ) {putExtra("type", Common.terms)}
        }
    }

    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    private fun configureGoogleSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }


    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                firebaseAuth = FirebaseAuth.getInstance()
                verifyIfUserAlreadyExistsInDataBase()
            } else {
                hideBsProgress()
                Toast.makeText(this, "Login com google falhou, tente novamente mais tarde!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun verifyIfUserAlreadyExistsInDataBase() {
         db.collection("users")
             .document(FirebaseAuth.getInstance().currentUser!!.uid)
             .get()
             .addOnSuccessListener {Log.i("teste",it.id)
                 if (it["name"]==null){
                     insertNewUserIntoDatabase()
                 } else {
                     updateToken()
                     hideBsProgress()
                     sharedPref.edit().putString("userID",it.id).apply()
                     sharedPref.edit().putString(email,it.getString("email")).apply()
                     sharedPref.edit().putString(fullname,it.getString("name")).apply()
                     sharedPref.edit().putString("photoUrl",FirebaseAuth.getInstance().currentUser!!.photoUrl.toString()).apply()
                     openActivity<MainActivity>(
                         finishWhenOpen = true,
                         enterAnim =  R.anim.nav_default_pop_enter_anim,
                         exitAnim =  R.anim.nav_default_pop_exit_anim
                     ) {  }
                 }
             }
             .addOnFailureListener {Log.i("teste",it.localizedMessage)
                hideBsProgress()
             }
    }

    private fun updateToken() {
        val token = FirebaseInstanceId.getInstance().token
        val uid = FirebaseAuth.getInstance().uid

        if(uid!=null){

            FirebaseFirestore.getInstance().collection(Common.UsersCollection).document(uid).update("token",token)

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
                hideBsProgress()
                Toast.makeText(this, "Erro: "+e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    // insere usuario no firebase
    private fun insertNewUserIntoDatabase() {
        val userData =
            User(
                firebaseAuth.currentUser!!.uid,
                firebaseAuth.currentUser!!.displayName!!, firebaseAuth.currentUser!!.email!!,"student"
            )

        val ref = db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
        ref.set(userData)
            .addOnSuccessListener {
                Log.i(
                    "Success",
                    userData.id+userData.name
                )
                sharedPref.edit().putString("userID",userData.id).apply()
                sharedPref.edit().putString(email,userData.email).apply()
                sharedPref.edit().putString(fullname,userData.name).apply()
                sharedPref.edit().putString("photoUrl",FirebaseAuth.getInstance().currentUser!!.photoUrl.toString()).apply()

                openActivity<MainActivity>(
                    finishWhenOpen = true,
                    enterAnim =  R.anim.nav_default_pop_enter_anim,
                    exitAnim =  R.anim.nav_default_pop_exit_anim
                ) {  }
                hideBsProgress()
            }
            .addOnFailureListener { Log.e("Error", userData.id)
                hideBsProgress()
            }
    }

}
