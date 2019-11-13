package com.luccas.passelivredocumentos.ui.formcollegeinformation

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.lifecycle.Observer
import com.google.android.gms.common.util.ArrayUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.luccas.passelivredocumentos.R
import com.luccas.passelivredocumentos.ui.base.BaseFragment
import com.luccas.passelivredocumentos.ui.formaddress.FormAddressFragment
import com.luccas.passelivredocumentos.utils.MaskUtils
import kotlinx.android.synthetic.main.dialog_progress.view.*
import kotlinx.android.synthetic.main.form_college_information_fragment.*
import java.lang.Exception

class FormCollegeInformationFragment : BaseFragment<FormCollegeInformationViewModel>() {

    private var periodStart: String = ""
    private var periodEnd: String = ""
    private var schoolSemester: String = ""
    private var collegeName: String = ""
    private var level: String =""
    private var turno: ArrayList<String> = ArrayList()
    private var days: ArrayList<String> = ArrayList()

    override val layoutRes = R.layout.form_college_information_fragment
    override fun getViewModel() = FormCollegeInformationViewModel::class.java

    companion object {
        fun newInstance() = FormCollegeInformationFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            activity!!.onBackPressed()
        }
        edt_period_start.addTextChangedListener(MaskUtils.insert(MaskUtils.PERIOD_MASK,edt_period_start))
        edt_period_end.addTextChangedListener(MaskUtils.insert(MaskUtils.PERIOD_MASK,edt_period_end))
        bt_next.setOnClickListener {
            if (validateForm()){
                showBottomSheetProgress()
                viewModel.sendFormToApi(
                    sharedPref.getString("userID","")!!,
                    collegeName,
                    periodStart,
                    periodEnd,
                    schoolSemester,
                    days,
                    level,
                    turno
                ).observe(this, Observer {
                    hideBsProgress()
                    activity!!.supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_from_right,R.anim.slide_to_left,R.anim.slide_from_left,R.anim.slide_to_right)
                        .replace(R.id.container, FormAddressFragment.newInstance())
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commitAllowingStateLoss()
                })
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
            if(isChecked){
                when(checkedId){
                    R.id.bt_manha -> turno.add("Manhã")
                    R.id.bt_tarde -> turno.add("Tarde")
                    R.id.bt_noite -> turno.add("Noite")
                }
            } else {
                try {
                    when(checkedId){
                        R.id.bt_manha -> turno.remove("Manhã")
                        R.id.bt_tarde -> turno.remove("Tarde")
                        R.id.bt_noite -> turno.remove("Noite")
                    }
                } catch (exception : Exception){
                    Log.i("error: ",exception.localizedMessage)
                }
            }
        }

        viewModel.getEducationalEstablishment(sharedPref.getString("userID","")!!).observe(this, Observer {
            progress_bar.visibility = View.GONE
            ln_main.visibility = View.VISIBLE
            if (it!=null){
                try {
                    edt_college_name.setText(it.name)
                    edt_period_start.setText(it.semesterPeriodStart)
                    edt_period_end.setText(it.semesterPeriodEnd)
                    edt_semester_number.setText(it.schoolSemester)
                    when(it.level){
                        "Fundamental" -> btn_fundamental.isChecked = true
                        "Médio" -> btn_medio.isChecked = true
                        "Técnico" -> btn_tecnico.isChecked = true
                        "Eja presencial" -> btn_eja.isChecked = true
                        "Pré-vestibular" -> btn_pre_vestibular.isChecked = true
                        "Superior" -> btn_superior.isChecked = true
                    }
                    for(day in it.schoolDays!!){
                        when(day){
                            "Segunda" -> bt_segunda.isChecked = true
                            "Terça" -> bt_terca.isChecked = true
                            "Quarta" -> bt_quarta.isChecked = true
                            "Quinta" -> bt_quinta.isChecked = true
                            "Sexta" -> bt_sexta.isChecked = true
                            "Sábado" -> bt_sabado.isChecked = true
                        }
                    }
                    for(day in it.turn!!){
                        when(day){
                            "Manhã" -> bt_manha.isChecked = true
                            "Tarde" -> bt_tarde.isChecked = true
                            "Noite" -> bt_noite.isChecked = true
                        }
                    }
                } catch (e : Exception){
                    Snackbar.make(scrollView,e.localizedMessage,Snackbar.LENGTH_SHORT).show()
                }
            }
        })
        viewModel.errorMessage.observe(this, Observer{
            hideBsProgress()
            progress_bar.visibility = View.GONE
            ln_main.visibility = View.VISIBLE
            Snackbar.make(scrollView,it,Snackbar.LENGTH_SHORT).show()
        })
    }

    private fun validateForm():Boolean {
        var isValid = true
         collegeName = edt_college_name.text.toString()
         periodStart = edt_period_start.text.toString()
         schoolSemester = edt_semester_number.text.toString()
         periodEnd = edt_period_end.text.toString()


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

        if (schoolSemester.isEmpty()){
            Snackbar.make(scrollView,"Digite qual o seu semestre atual!",Snackbar.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

    private fun hideBsProgress() {
        if (bsProgress!=null){
            bsProgress!!.dismiss()
        }
    }

    private var sProgress: View? = null
    private var bsProgress: BottomSheetDialog? = null
    private fun showBottomSheetProgress() {
        bsProgress  = BottomSheetDialog(ContextThemeWrapper(context!!, R.style.DialogSlideAnim))
        sProgress = layoutInflater.inflate(R.layout.dialog_progress, null)
        bsProgress!!.setContentView(sProgress!!)
        bsProgress!!.window!!.decorView.setBackgroundResource(android.R.color.transparent)
        bsProgress!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        bsProgress!!.setCancelable(false)
        bsProgress!!.show()
        sProgress!!.tv_message.text = "Estamos enviando suas informações..."
    }
}
