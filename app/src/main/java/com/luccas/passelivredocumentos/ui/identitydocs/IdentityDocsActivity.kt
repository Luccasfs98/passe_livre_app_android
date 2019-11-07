package com.luccas.passelivredocumentos.ui.identitydocs

import android.os.Bundle
import com.luccas.passelivredocumentos.R
import com.luccas.passelivredocumentos.databinding.IdentityDocsBinding
import com.luccas.passelivredocumentos.ui.base.BaseActivity
import com.luccas.passelivredocumentos.ui.base.UploadAndRemoveFileViewModel

class IdentityDocsActivity : BaseActivity<UploadAndRemoveFileViewModel>() {

    override val layoutRes = R.layout.identity_docs_activity

    override fun getViewModel() = UploadAndRemoveFileViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, IdentityDocsFragment.newInstance())
                .commitNow()
        }
    }

}
