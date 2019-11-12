package com.luccas.passelivredocumentos.ui.formpersonaldata

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.luccas.passelivredocumentos.FormCollegeInformationActivity
import com.luccas.passelivredocumentos.R
import com.luccas.passelivredocumentos.ui.base.BaseFragment
import com.luccas.passelivredocumentos.ui.formcollegeinformation.FormCollegeInformationFragment
import com.luccas.passelivredocumentos.utils.MaskUtils
import com.luccas.passelivredocumentos.utils.openActivity
import kotlinx.android.synthetic.main.form_personal_data_fragment.*

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

        toggle_group_sex.addOnButtonCheckedListener { group, checkedId, isChecked ->
            Log.i("BTN SELECTED NAME", checkedId.toString())

        }

        bt_next.setOnClickListener {
            if(validateForm()){
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
                viewModel.sendFormToApi(name,nameFather,nameMother,dateBirthday,cpf,sex,type).observe(this, Observer {

                    /*activity!!.openActivity<FormCollegeInformationActivity>(
                        enterAnim = R.anim.slide_from_right,
                        exitAnim = R.anim.slide_to_left
                    )*/

                    activity!!.supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_from_right,R.anim.slide_to_left,R.anim.slide_from_left,R.anim.slide_to_right)
                        .replace(R.id.container, FormCollegeInformationFragment.newInstance())
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commitAllowingStateLoss()
                })
            }
        }
    }

    private fun validateForm(): Boolean {
        val name = edt_name.text.toString()
        val nameFather = edt_father_name.text.toString()
        val nameMother = edt_mother_name.text.toString()
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
