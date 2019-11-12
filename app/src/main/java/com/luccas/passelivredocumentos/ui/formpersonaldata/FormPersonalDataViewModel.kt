package com.luccas.passelivredocumentos.ui.formpersonaldata

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class FormPersonalDataViewModel @Inject constructor() : ViewModel() {

    lateinit var success : MutableLiveData<Boolean>
    fun sendFormToApi(
        name: String,
        nameFather: String,
        nameMother: String,
        dateBirthday: String,
        cpf: String,
        sex: String,
        type: String
    ) : MutableLiveData<Boolean> {
        success = MutableLiveData()
        success.value = true
        return success
    }
}
