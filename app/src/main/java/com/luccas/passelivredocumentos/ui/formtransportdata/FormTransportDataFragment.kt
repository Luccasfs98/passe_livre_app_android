package com.luccas.passelivredocumentos.ui.formtransportdata

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.luccas.passelivredocumentos.R
import com.luccas.passelivredocumentos.ui.base.BaseFragment
import com.luccas.passelivredocumentos.ui.identitydocs.IdentityDocsActivity
import com.luccas.passelivredocumentos.utils.Common
import com.luccas.passelivredocumentos.utils.openActivity
import kotlinx.android.synthetic.main.form_transport_data_fragment.*

class FormTransportDataFragment : BaseFragment<FormTransportDataViewModel>() {

    companion object {
        fun newInstance() = FormTransportDataFragment()
    }

    private var alreadyHavePasseLivre : Boolean ? = false
    private var prouniScholarshipHolder : Boolean ? = false
    private var useIntegration : Boolean ? = false


    private var line: String = ""
    private var transportName: String = ""
    override val layoutRes: Int
        get() = R.layout.form_transport_data_fragment
    override fun getViewModel() = FormTransportDataViewModel::class.java
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            activity!!.onBackPressed()
        }
        bt_next.setOnClickListener {
            if(validateFields()){
                showBottomSheetProgress()
                viewModel.sendFormToApi(FirebaseAuth.getInstance().currentUser!!.uid,transportName,line,alreadyHavePasseLivre,useIntegration,prouniScholarshipHolder).observe(this, Observer {
                    val edit = sharedPref.edit()
                    edit.putString(Common.transportName,transportName)
                    edit.putString(Common.line,line)
                    edit.putBoolean(Common.alreadyHavePasseLivre,alreadyHavePasseLivre!!)
                    edit.putBoolean(Common.useIntegration,useIntegration!!)
                    edit.putBoolean(Common.prouniScholarshipHolder,prouniScholarshipHolder!!)
                    edit.apply()

                    Handler().postDelayed({
                        hideBsProgress()
                        activity!!.openActivity<IdentityDocsActivity> (
                            enterAnim = R.anim.slide_from_right,
                            exitAnim = R.anim.slide_from_left
                        ){  }
                    },2000)



                })
            }
        }

        toggle_group_questions.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked){
                when(checkedId){
                    R.id.bt_integration -> useIntegration = true
                    R.id.bt_already_has_pass_in_last_year -> alreadyHavePasseLivre = true
                    R.id.bt_prouni-> prouniScholarshipHolder = true
                }
            } else {
                when(checkedId){
                    R.id.bt_integration -> useIntegration = false
                    R.id.bt_already_has_pass_in_last_year -> alreadyHavePasseLivre = false
                    R.id.bt_prouni -> prouniScholarshipHolder = false
                }
            }
        }

        viewModel.getTransportData(FirebaseAuth.getInstance().currentUser!!.uid).observe(this, Observer {

            progress_bar.visibility = View.GONE
            ln_main.visibility = View.VISIBLE

            if (it!=null){
                edt_transport_name.setText(it.transportName)
                edt_line.setText(it.line)

                if (it.prouniScholarshipHolder!!){
                    bt_prouni.isChecked = true
                }
                if (it.useIntegration!!){
                    bt_integration.isChecked = true
                }
                if (it.alreadyHavePasseLivre!!){
                    bt_already_has_pass_in_last_year.isChecked = true
                }
            }
        })

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
