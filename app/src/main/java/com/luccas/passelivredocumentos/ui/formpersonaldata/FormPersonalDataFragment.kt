package com.luccas.passelivredocumentos.ui.formpersonaldata

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.luccas.passelivredocumentos.FormCollegeInformationActivity
import com.luccas.passelivredocumentos.R
import com.luccas.passelivredocumentos.ui.base.BaseFragment
import com.luccas.passelivredocumentos.ui.formcollegeinformation.FormCollegeInformationFragment
import com.luccas.passelivredocumentos.utils.Common
import com.luccas.passelivredocumentos.utils.MaskUtils
import com.luccas.passelivredocumentos.utils.openActivity
import kotlinx.android.synthetic.main.dialog_progress.view.*
import kotlinx.android.synthetic.main.form_personal_data_fragment.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class FormPersonalDataFragment : BaseFragment<FormPersonalDataViewModel>() {

    override val layoutRes: Int
        get() = R.layout.form_personal_data_fragment

    override fun getViewModel() = FormPersonalDataViewModel::class.java
    companion object {
        fun newInstance() = FormPersonalDataFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            activity!!.onBackPressed()
        }

        edt_cpf.addTextChangedListener(MaskUtils.insert(MaskUtils.CPF_MASK,edt_cpf))
        edt_date.addTextChangedListener(MaskUtils.insert(MaskUtils.BIRTH_MASK,edt_date))
        edt_phone_number.addTextChangedListener(MaskUtils.insert(MaskUtils.PHONE_MASK,edt_phone_number))

        toggle_group_sex.addOnButtonCheckedListener { group, checkedId, isChecked ->
            Log.i("BTN SELECTED NAME", checkedId.toString())

        }

        bt_next.setOnClickListener {
            if(validateForm()){

                showBottomSheetProgress()
                val phone = edt_phone_number.text.toString()
                val name = edt_name.text.toString()
                val nameFather = edt_father_name.text.toString()
                val nameMother = edt_mother_name.text.toString()
                val dateBirthday = edt_date.text.toString()
                val cpf = edt_cpf.text.toString()
                var sex : String = if (toggle_group_sex.checkedButtonId == R.id.btn_masc){
                    "Masculino"
                } else {
                    "Feminino"
                }
                var btId = toggle_grou_type_solicitation.checkedButtonId
                var type : String = if (btId==R.id.bt_renovation_with_Card){
                    "Renovação anual com cartão"
                } else if(btId == R.id.bt_renovation_without_Card){
                    "Renovação anual sem cartão"
                } else if (btId == R.id.bt_new_user){
                    "Usuário novo"
                } else {
                    "Segunda via"
                }
                viewModel.sendFormToApi(FirebaseAuth.getInstance().currentUser!!.uid,name,phone,nameFather,nameMother,dateBirthday,cpf,sex,type).observe(this, Observer {

                    val edit = sharedPref.edit()
                    edit.putString(Common.fullname,name)
                    edit.putString(Common.phone,phone)
                    edit.putString(Common.nameFather,nameFather)
                    edit.putString(Common.nameMother,nameMother)
                    edit.putString(Common.dateBirthday,dateBirthday)
                    edit.putString(Common.cpf,cpf)
                    edit.putString(Common.sex,sex)
                    edit.putString(Common.type,type)
                    edit.apply()

                    Handler().postDelayed({
                        hideBsProgress()
                        activity!!.supportFragmentManager.beginTransaction()
                            .setCustomAnimations(
                                R.anim.slide_from_right,
                                R.anim.slide_to_left,
                                R.anim.slide_from_left,
                                R.anim.slide_to_right
                            )
                            .replace(R.id.container, FormCollegeInformationFragment.newInstance())
                            .setReorderingAllowed(true)
                            .addToBackStack(null)
                            .commitAllowingStateLoss()
                    },3000)
                })
            }
        }

        viewModel.getPersonalData(FirebaseAuth.getInstance().currentUser!!.uid).observe(this, Observer {
            if(it!=null) {
                edt_name.setText(it.name)
                edt_father_name.setText(it.nameFather)
                edt_mother_name.setText(it.nameMother)
                edt_phone_number.setText(it.phone)
                edt_date.setText(it.dateBirthday)
                edt_cpf.setText(it.cpf)

                if (it.sex == "Masculino") {
                    btn_masc.isChecked = true
                } else {
                    btn_fem.isChecked = true
                }

                when (it.type) {
                    "Renovação anual com cartão" -> bt_renovation_with_Card.isChecked = true
                    "Renovação anual sem cartão" -> bt_renovation_without_Card.isChecked = true
                    "Usuário novo" -> bt_new_user.isChecked = true
                    "Segunda via" -> bt_second_way.isChecked = true
                }
            }
            progress_bar.visibility = View.GONE
            ln_main.visibility = View.VISIBLE
        })
        viewModel.errorMessage.observe(this,Observer {
            hideBsProgress()
            Snackbar.make(scrollView,it,Snackbar.LENGTH_SHORT).show()
            progress_bar.visibility = View.GONE
            ln_main.visibility = View.VISIBLE
        })

    }

    private fun validateForm(): Boolean {
        val name = edt_name.text.toString()
        val nameFather = edt_father_name.text.toString()
        val nameMother = edt_mother_name.text.toString()
        val phone = edt_phone_number.text.toString()
        val dateBirthday = edt_date.text.toString()
        val cpf = edt_cpf.text.toString()
        if (name.isEmpty()){
            til_name.error = "Seu nome não foi preenchido!"
            scrollView.smoothScrollTo(0,0)
            return false
        }  else {
            til_name.error = null
        }
        if (cpf.isEmpty() || cpf.length !=14) {
            til_cpf.error = "CPF incorreto."
            scrollView.smoothScrollTo(0,0)
            return false
        } else {
            til_cpf.error = null
        }

        if (dateBirthday.isEmpty() || dateBirthday.length !=10) {
            til_date.error = "Data de nascimento incorreta!"
            scrollView.smoothScrollTo(0,0)
            return false
        } else {
            til_date.error = null
        }

        if (nameFather.isEmpty()) {
            til_father.error = "Nome do pai incorreto!"
            scrollView.smoothScrollTo(0,0)
            return false
        } else {
            til_father.error = null
        }
        if (MaskUtils.unmask(phone).length!=11){
            til_phone_number.error = "Número de celular inválido"
            return false
        } else {
            til_phone_number.error = null
        }

        if (nameMother.isEmpty()) {
            til_mother.error = "Nome da mãe incorreto!"
            scrollView.smoothScrollTo(0,0)
            return false
        } else {
            til_mother.error = null
        }
        Log.i("BTN SELECTED NAME", toggle_group_sex.checkedButtonId.toString())
        if (toggle_group_sex.checkedButtonId==-1){
            Snackbar.make(scrollView,"Selecione seu sexo",Snackbar.LENGTH_SHORT).show()
            return false
        }

        if (toggle_grou_type_solicitation.checkedButtonId==-1){
            Snackbar.make(scrollView,"Selecione o tipo da solicitação",Snackbar.LENGTH_SHORT).show()
            return false
        }
        return true
    }


}
