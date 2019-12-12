package com.luccas.passelivredocumentos.ui

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.luccas.passelivredocumentos.R
import com.luccas.passelivredocumentos.ui.login.AuthActivity
import com.luccas.passelivredocumentos.utils.Common
import com.luccas.passelivredocumentos.utils.openActivity

class SplashScreenActivity : AppCompatActivity() {
    lateinit var sharedPref: SharedPreferences

    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        sharedPref = getSharedPreferences("App", 0)
    }

    override fun onResume() {
        super.onResume()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            verifyIfUserAlreadyExistsInDataBase()
        } else {
            openAuthActivity()
        }
    }

    private fun openAuthActivity() {
        openActivity<AuthActivity>(
            finishWhenOpen = true,
            enterAnim =  R.anim.nav_default_pop_enter_anim,
            exitAnim =  R.anim.nav_default_pop_exit_anim
        ) {  }
    }

    private fun verifyIfUserAlreadyExistsInDataBase() {
        db.collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .get()
            .addOnSuccessListener {
                Log.i("teste",it.id)
                if (it["name"]==null){
                   openAuthActivity()
                } else {
                    updateToken()
                    sharedPref.edit().putString("userID",it.id).apply()
                    sharedPref.edit().putString(Common.email,it.getString("email")).apply()
                    sharedPref.edit().putString(Common.fullname,it.getString("name")).apply()
                    sharedPref.edit().putString("photoUrl",FirebaseAuth.getInstance().currentUser!!.photoUrl.toString()).apply()
                    openActivity<MainActivity>(
                        finishWhenOpen = true,
                        enterAnim =  R.anim.nav_default_pop_enter_anim,
                        exitAnim =  R.anim.nav_default_pop_exit_anim
                    ) {  }
                }
            }
            .addOnFailureListener {
                openAuthActivity()

                Log.i("teste",it.localizedMessage)
            }
    }
    private fun updateToken() {
        val token = FirebaseInstanceId.getInstance().token
        val uid = FirebaseAuth.getInstance().uid

        if(uid!=null){

            FirebaseFirestore.getInstance().collection(Common.UsersCollection).document(uid).update("token",token)

        }

    }
}
