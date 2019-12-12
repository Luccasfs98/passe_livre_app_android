package com.luccas.passelivredocumentos.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.luccas.passelivredocumentos.R
import com.luccas.passelivredocumentos.ui.base.BaseActivity
import com.luccas.passelivredocumentos.ui.login.AuthActivity
import com.luccas.passelivredocumentos.utils.Common.Companion.email
import com.luccas.passelivredocumentos.utils.Common.Companion.fullname
import com.luccas.passelivredocumentos.utils.openActivity


class MainActivity : BaseActivity<MainViewModel>() {

    override val layoutRes = R.layout.activity_main
    override fun getViewModel() = MainViewModel::class.java
    private lateinit var appBarConfiguration: AppBarConfiguration


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val headerView = navView.getHeaderView(0)
        navView.menu.findItem(R.id.nav_quit).setOnMenuItemClickListener {
            configureGoogleSignIn()
            FirebaseAuth.getInstance().signOut()
            mGoogleSignInClient.signOut()
            openActivity<AuthActivity> (finishWhenOpen = true,enterAnim = R.anim.anim_fade_in,exitAnim = R.anim.anim_fade_out){  }
            true
        }

        Glide.with(this)
            .asBitmap()
            .load(sharedPref.getString("photoUrl",""))
            .error(R.drawable.ic_profile_pic_default)
            .into(headerView.findViewById(R.id.iv_profile_pic)
        )

        headerView.findViewById<TextView>(R.id.tv_email).text = sharedPref.getString(email,"")
        headerView.findViewById<TextView>(R.id.tv_title).text = sharedPref.getString(fullname,"")

    }
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions

    private fun configureGoogleSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
