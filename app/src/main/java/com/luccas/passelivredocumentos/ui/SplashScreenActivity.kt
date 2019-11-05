package com.luccas.passelivredocumentos.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.luccas.passelivredocumentos.R
import com.luccas.passelivredocumentos.ui.login.AuthActivity
import com.luccas.passelivredocumentos.utils.openActivity

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val h = Handler()
        h.postDelayed({
            openActivity<AuthActivity> (
                finishWhenOpen = true,
                enterAnim = R.anim.slide_from_right,
                exitAnim = R.anim.slide_to_left
            ){
            }
        },3000)
    }
}
