package com.luccas.passelivredocumentos

import android.os.Bundle
import com.luccas.passelivredocumentos.ui.base.BaseActivity
import com.luccas.passelivredocumentos.ui.solicitationmoredetails.SolicitationMoreDetailsFragment
import com.luccas.passelivredocumentos.ui.solicitationmoredetails.SolicitationMoreDetailsViewModel

class SolicitationMoreDetails : BaseActivity<SolicitationMoreDetailsViewModel>() {

    override fun getViewModel() = SolicitationMoreDetailsViewModel::class.java
    override val layoutRes = R.layout.solicitation_more_details_activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, SolicitationMoreDetailsFragment.newInstance())
                .commitNow()
        }
    }

}
