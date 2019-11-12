package com.luccas.passelivredocumentos.ui.formaddress

import android.os.Bundle

import com.luccas.passelivredocumentos.R
import com.luccas.passelivredocumentos.ui.base.BaseFragment
import com.luccas.passelivredocumentos.ui.formtransportdata.FormTransportDataFragment
import com.luccas.passelivredocumentos.utils.MaskUtils
import kotlinx.android.synthetic.main.form_address_fragment.*

class FormAddressFragment : BaseFragment<FormAddressViewModel>() {

    companion object {
        fun newInstance() = FormAddressFragment()
    }

    private  var uf: String =""
    private  var neighborhood: String=""
    private  var city: String=""
    private  var number: String=""
    private  var complement: String=""
    private  var address: String=""
    private  var cep: String=""
    override val layoutRes: Int
        get() = R.layout.form_address_fragment

    override fun getViewModel() = FormAddressViewModel::class.java

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            activity!!.onBackPressed()
        }

        edt_cep.addTextChangedListener(MaskUtils.insert(MaskUtils.CEP_MASK,edt_cep))

        bt_next.setOnClickListener {
            if (validateFields()){
                activity!!.supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_from_right,R.anim.slide_to_left,R.anim.slide_from_left,R.anim.slide_to_right)
                    .replace(R.id.container, FormTransportDataFragment.newInstance())
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commitAllowingStateLoss()
            }
        }

    }

    private fun validateFields(): Boolean {
        var isValid = true
         cep = edt_cep.text.toString()
         address = edt_address.text.toString()
         complement = edt_complement.text.toString()
         city = edt_city.text.toString()
         number = edt_number.text.toString()
         neighborhood = edt_neighborhood.text.toString()
         uf = edt_neighborhood.text.toString()
        if (cep.length!=9){
            til_cep.error = "Cep inválido!"
            isValid = false
        } else {
            til_cep.error = null
        }
        if (address.isEmpty()){
            til_address.error = "Endereço inválido!"
            isValid = false
        } else {
            til_address.error = null
        }
        if (complement.isEmpty()){
            til_complement.error = "Complemento inválido!"
            isValid = false
        } else {
            til_complement.error = null
        }
         if (number.isEmpty()){
            til_number.error = "Número inválido!"
            isValid = false
         } else {
            til_number.error = null
         }
        if (city.isEmpty()){
            til_city.error = "Cidade inválida!"
            isValid = false
        } else {
            til_city.error = null
        }
        if (neighborhood.isEmpty()){
            til_neighborhood.error = "Bairro inválido!"
            isValid = false
        } else {
            til_neighborhood.error = null
        }
        if (uf.isEmpty()){
            til_uf.error = "Bairro inválido!"
            isValid = false
        } else {
            til_uf.error = null
        }

        return isValid
    }

}
