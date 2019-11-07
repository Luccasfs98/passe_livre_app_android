package com.luccas.passelivredocumentos.ui.checkingcopy

import android.os.Bundle
import com.luccas.passelivredocumentos.R
import com.luccas.passelivredocumentos.ui.base.BaseActivity

class CheckingCopyActivity : BaseActivity<CheckingCopyViewModel>() {

    override val layoutRes = R.layout.checking_copy_activity
    override fun getViewModel() = CheckingCopyViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, CheckingCopyFragment.newInstance())
                .commitNow()
        }
    }

}
