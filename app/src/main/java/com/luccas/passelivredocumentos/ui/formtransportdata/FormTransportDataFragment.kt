package com.luccas.passelivredocumentos.ui.formtransportdata

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.luccas.passelivredocumentos.R
import com.luccas.passelivredocumentos.ui.base.BaseFragment
import com.luccas.passelivredocumentos.ui.identitydocs.IdentityDocsActivity
import com.luccas.passelivredocumentos.ui.identitydocs.IdentityDocsFragment
import com.luccas.passelivredocumentos.utils.openActivity
import kotlinx.android.synthetic.main.form_transport_data_fragment.*

class FormTransportDataFragment : BaseFragment<FormTransportDataViewModel>() {

    companion object {
        fun newInstance() = FormTransportDataFragment()
    }

    private var line: String = ""
    private var transportName: String = ""
    override val layoutRes: Int
        get() = R.layout.form_transport_data_fragment
    override fun getViewModel() = FormTransportDataViewModel::class.java

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            activity!!.onBackPressed()
        }
        bt_next.setOnClickListener {
            if(validateFields()){
                activity!!.openActivity<IdentityDocsActivity> (
                    enterAnim = R.anim.slide_from_right,
                    exitAnim = R.anim.slide_from_left
                ){  }
                /*activity!!.supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_from_right,R.anim.slide_to_left,R.anim.slide_from_left,R.anim.slide_to_right)
                    .replace(R.id.container, IdentityDocsFragment.newInstance())
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commitAllowingStateLoss()*/
            }
        }
    }

    private fun validateFields(): Boolean {
        var isValid = true
        transportName = edt_transport_name.text.toString()
        line = edt_line.text.toString()

        if (transportName.isEmpty()){
            til_transport_name.error = "Empresa de transporte inválida!"
            isValid = false
        } else {
            til_transport_name.error = null
        }
        if (line.isEmpty()){
            til_line.error = "Linha inválida!"
            isValid = false
        } else {
            til_line.error = null
        }

        return isValid
    }

}
