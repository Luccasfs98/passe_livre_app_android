package com.luccas.passelivredocumentos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.luccas.passelivredocumentos.ui.terms.TermsFragment

class TermsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.terms_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, TermsFragment.newInstance())
                .commitNow()
        }
    }

}
