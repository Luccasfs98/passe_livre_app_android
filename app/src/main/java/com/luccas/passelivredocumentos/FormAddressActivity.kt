package com.luccas.passelivredocumentos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.luccas.passelivredocumentos.ui.base.BaseActivity
import com.luccas.passelivredocumentos.ui.formaddress.FormAddressFragment
import com.luccas.passelivredocumentos.ui.formaddress.FormAddressViewModel

class FormAddressActivity : BaseActivity<FormAddressViewModel>() {

    override val layoutRes: Int
        get() = R.layout.form_address_activity

    override fun getViewModel() = FormAddressViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, FormAddressFragment.newInstance())
                .commitNow()
        }
    }

}
