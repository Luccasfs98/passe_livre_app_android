package com.luccas.passelivredocumentos.ui.formtransportdata

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
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

    private var userLine: String? = null
    private lateinit var transports: ArrayList<String>
    private lateinit var lines: ArrayList<String>
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

        viewModel.errorMessage.observe(this, Observer {
            Snackbar.make(formpersonaldata,it,Snackbar.LENGTH_SHORT).show()
        })
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


        viewModel.getTransports().observe(this, Observer {
            if(it!=null){
                transports = ArrayList()
                transports.add("Empresa de transporte")
                for(transport in it){
                    transports.add(transport["nome"].toString())
                }

                val arrayAdapter = ArrayAdapter<String>(context!!,android.R.layout.simple_spinner_dropdown_item,transports)
                sp_transports.adapter = arrayAdapter
                lines = ArrayList()
                lines.add("Linha")
                sp_transports.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        if(position!=0){
                            for(line in it[position-1]["rotas"] as ArrayList<String>){
                                lines.add(line)
                            }
                            val arrayAdapter = ArrayAdapter<String>(context!!,android.R.layout.simple_spinner_dropdown_item,lines)
                            sp_lines.adapter = arrayAdapter
                            sp_lines.visibility = View.VISIBLE

                            if (lines!=null && userLine!=null)
                                sp_lines.setSelection(lines.indexOf(userLine!!))

                        } else {
                            sp_lines.visibility = View.GONE
                            sp_lines.adapter = null
                        }
                    }
                }
            }
            getTransportData()

        })



    }

    private fun getTransportData() {
        viewModel.getTransportData(FirebaseAuth.getInstance().currentUser!!.uid).observe(this, Observer {
            progress_bar.visibility = View.GONE
            ln_main.visibility = View.VISIBLE
            bt_next.visibility = View.VISIBLE
            tv_integration_info.visibility = View.VISIBLE

            if (it!=null){

                if (transports!=null)
                    sp_transports.setSelection(transports.indexOf(it.transportName))

                userLine = it.line

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
        if(sp_transports.selectedItem==null || sp_transports.selectedItem.toString() == "Empresa de transporte"){
            Snackbar.make(formpersonaldata,"Selecione uma empresa!",Snackbar.LENGTH_SHORT).show()
            return false
        } else {
            transportName = sp_transports.selectedItem.toString()
        }
        if (sp_lines.selectedItem == null || sp_lines.selectedItem.toString() == "Linha"){
            Snackbar.make(formpersonaldata,"Selecione uma linha!",Snackbar.LENGTH_SHORT).show()
            return false
        } else {
            line = sp_lines.selectedItem.toString()
        }
        return true
    }

}
