package com.luccas.passelivredocumentos.ui.formcollegeinformation

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.common.util.ArrayUtils
import com.google.android.material.snackbar.Snackbar
import com.luccas.passelivredocumentos.R
import com.luccas.passelivredocumentos.ui.base.BaseFragment
import com.luccas.passelivredocumentos.ui.formaddress.FormAddressFragment
import com.luccas.passelivredocumentos.ui.formpersonaldata.FormPersonalDataFragment
import com.luccas.passelivredocumentos.utils.MaskUtils
import kotlinx.android.synthetic.main.form_college_information_fragment.*
import java.lang.Exception

class FormCollegeInformationFragment : BaseFragment<FormCollegeInformationViewModel>() {

    private var turno: String?=null
    private var days: ArrayList<String>?=ArrayList()
    private var level: String?=null
    override val layoutRes = R.layout.form_college_information_fragment
    override fun getViewModel() = FormCollegeInformationViewModel::class.java

    companion object {
        fun newInstance() = FormCollegeInformationFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            activity!!.onBackPressed()
        }
        edt_phone_number.addTextChangedListener(MaskUtils.insert(MaskUtils.PHONE_MASK,edt_phone_number))
        edt_period_start.addTextChangedListener(MaskUtils.insert(MaskUtils.PERIOD_MASK,edt_period_start))
        edt_period_end.addTextChangedListener(MaskUtils.insert(MaskUtils.PERIOD_MASK,edt_period_end))
        bt_next.setOnClickListener {
            if (validateForm()){
                activity!!.supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_from_right,R.anim.slide_to_left,R.anim.slide_from_left,R.anim.slide_to_right)
                    .replace(R.id.container, FormAddressFragment.newInstance())
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commitAllowingStateLoss()
            }
        }

        toggle_group_level.addOnButtonCheckedListener { group, checkedId, isChecked ->
            when(checkedId){

                R.id.btn_fundamental -> level = "Fundamental"
                R.id.btn_medio -> level = "Médio"
                R.id.btn_tecnico -> level = "Técnico"
                R.id.btn_eja -> level = "Eja presencial"
                R.id.btn_pre_vestibular -> level = "Pré-vestibular"
                R.id.btn_superior -> level = "Superior"

            }

        }
        toggle_days.addOnButtonCheckedListener { group, checkedId, isChecked ->

            if(isChecked){

                when(checkedId){
                    R.id.bt_segunda -> days!!.add("Segunda")
                    R.id.bt_terca -> days!!.add("Terça")
                    R.id.bt_quarta -> days!!.add("Quarta")
                    R.id.bt_quinta -> days!!.add("Quinta")
                    R.id.bt_sexta -> days!!.add("Sexta")
                    R.id.bt_sabado -> days!!.add("Sábado")
                }
            } else {
                try {
                    when(checkedId){
                        R.id.bt_segunda -> days!!.remove("Segunda")
                        R.id.bt_terca -> days!!.remove("Terça")
                        R.id.bt_quarta -> days!!.remove("Quarta")
                        R.id.bt_quinta -> days!!.remove("Quinta")
                        R.id.bt_sexta -> days!!.remove("Sexta")
                        R.id.bt_sabado -> days!!.remove("Sábado")
                    }
                } catch (exception : Exception){
                    Log.i("error: ",exception.localizedMessage)
                }
            }
        }
       toggle_group_turn.addOnButtonCheckedListener { _, checkedId, isChecked ->
            when(checkedId){
                R.id.bt_manha -> turno = "Manhã"
                R.id.bt_tarde -> turno = "Tarde"
                R.id.bt_noite -> turno = "Noite"
            }

        }
    }

    private fun validateForm():Boolean {
        var isValid = true
        val phone = edt_phone_number.text.toString()
        val collegeName = edt_college_name.text.toString()
        val periodStart = edt_period_start.text.toString()
        val periodEnd = edt_period_end.text.toString()

        if (MaskUtils.unmask(phone).length!=11){
            til_phone_number.error = "Número de celular inválido"
            isValid = false
        } else {
            til_phone_number.error = null
        }
        if(collegeName.isEmpty()) {
            til_college.error = "Digite o nome da sua instituição de ensino."
            isValid = false
        } else {
            til_college.error = null
        }

        if (periodStart.length != 5){
            til_period_start.error = "Digite o período"
            isValid = false
        } else {
            til_period_start.error = null
        }

        if (periodEnd.length != 5){
            til_period_end.error = "Digite o período"
            isValid = false
        } else {
            til_period_end.error = null
        }

        if (level==null){
            Snackbar.make(scrollView,"Selecione o seu nível de escolaridade",Snackbar.LENGTH_SHORT).show()
            isValid = false

        }

        if (days.isNullOrEmpty()){
            Snackbar.make(scrollView,"Selecione ao menos um dia em que estudará",Snackbar.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }
}
