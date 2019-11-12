package com.luccas.passelivredocumentos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.luccas.passelivredocumentos.ui.solicitationmoredetails.SolicitationMoreDetailsFragment

class SolicitationMoreDetails : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.solicitation_more_details_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, SolicitationMoreDetailsFragment.newInstance())
                .commitNow()
        }
    }

}
