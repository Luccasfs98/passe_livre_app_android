package com.luccas.passelivredocumentos.ui.formpersonaldata
import android.os.Bundle
import com.luccas.passelivredocumentos.R
import com.luccas.passelivredocumentos.ui.base.BaseActivity


class FormPersonalData : BaseActivity<FormPersonalDataViewModel>() {

    override val layoutRes: Int
        get() = R.layout.form_personal_data_fragment

    override fun getViewModel() = FormPersonalDataViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.form_personal_data_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, FormPersonalDataFragment.newInstance())
                .commitNow()
        }
    }
    override fun onBackPressed() {
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}
