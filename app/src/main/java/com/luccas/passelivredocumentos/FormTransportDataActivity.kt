package com.luccas.passelivredocumentos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.luccas.passelivredocumentos.ui.formtransportdata.FormTransportDataFragment

class FormTransportDataActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.form_transport_data_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, FormTransportDataFragment.newInstance())
                .commitNow()
        }
    }

}
