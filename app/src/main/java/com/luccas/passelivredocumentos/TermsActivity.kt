package com.luccas.passelivredocumentos

import android.os.Bundle
import com.luccas.passelivredocumentos.ui.base.BaseActivity
import com.luccas.passelivredocumentos.ui.terms.TermsFragment
import com.luccas.passelivredocumentos.ui.terms.TermsViewModel

class TermsActivity : BaseActivity<TermsViewModel>() {
    override val layoutRes = R.layout.terms_activity
    override fun getViewModel() = TermsViewModel::class.java
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.terms_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container_terms, TermsFragment())
                .commitNow()
        }
    }

}
