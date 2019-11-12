package com.luccas.passelivredocumentos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.luccas.passelivredocumentos.ui.formcollegeinformation.FormCollegeInformationFragment

class FormCollegeInformationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.form_college_information_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, FormCollegeInformationFragment.newInstance())
                .commitNow()
        }
    }

}
