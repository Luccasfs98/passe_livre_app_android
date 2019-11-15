package com.luccas.passelivredocumentos.ui.formpersonaldata

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.luccas.passelivredocumentos.models.PersonalDataDto
import com.luccas.passelivredocumentos.utils.Common.Companion.PersonalDataCollection
import com.luccas.passelivredocumentos.utils.Common.Companion.PersonalDataDocument
import com.luccas.passelivredocumentos.utils.Common.Companion.UsersCollection
import javax.inject.Inject

class FormPersonalDataViewModel @Inject constructor() : ViewModel() {

    lateinit var success : MutableLiveData<Boolean>
    lateinit var personalDataResponse : MutableLiveData<PersonalDataDto>
    var errorMessage : MutableLiveData<String> = MutableLiveData()
    fun sendFormToApi(
        idUser:String,
        name: String,
        phone:String,
        nameFather: String,
        nameMother: String,
        dateBirthday: String,
        cpf: String,
        sex: String,
        type: String
    ) : MutableLiveData<Boolean> {
        success = MutableLiveData()
        val instance: FirebaseFirestore = FirebaseFirestore.getInstance()

        instance.collection(UsersCollection).document(idUser).collection(PersonalDataCollection).document(
            PersonalDataDocument)
            .set(PersonalDataDto(name,phone,nameFather,nameMother,dateBirthday,cpf,sex,type))
            .addOnSuccessListener {
                success.value = true
            }
            .addOnFailureListener {
                errorMessage.value = it.localizedMessage
                success.value = false
            }

        return success
    }

    fun getPersonalData(idUser:String) : MutableLiveData<PersonalDataDto> {
        personalDataResponse = MutableLiveData()
        val instance: FirebaseFirestore = FirebaseFirestore.getInstance()
        instance
            .collection(UsersCollection)
            .document(idUser)
            .collection(PersonalDataCollection)
            .document(PersonalDataDocument)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                personalDataResponse.value = documentSnapshot.toObject(PersonalDataDto::class.java)
            }
            .addOnFailureListener {
                errorMessage.value = it.localizedMessage
            }

        return personalDataResponse
    }
}
