package com.luccas.passelivredocumentos.ui.formcollegeinformation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.luccas.passelivredocumentos.models.EducationalEstablishmentDto
import com.luccas.passelivredocumentos.models.PersonalDataDto
import com.luccas.passelivredocumentos.utils.Common.Companion.EducationalEstablishmentCollection
import com.luccas.passelivredocumentos.utils.Common.Companion.EducationalEstablishmentDocument
import com.luccas.passelivredocumentos.utils.Common.Companion.UsersCollection
import javax.inject.Inject

class FormCollegeInformationViewModel @Inject constructor(): ViewModel() {

    lateinit var success : MutableLiveData<Boolean>
    lateinit var personalDataResponse : MutableLiveData<PersonalDataDto>
    lateinit var establishmentDataResponse : MutableLiveData<EducationalEstablishmentDto>
    var errorMessage : MutableLiveData<String> = MutableLiveData()
    fun sendFormToApi(
        idUser:String,
        name: String,
        semesterPeriodStart: String,
        semesterPeriodEnd: String,
        schoolSemester: String,
        schoolDays: ArrayList<String>,
        level: String,
        turn: ArrayList<String>
    ) : MutableLiveData<Boolean> {
        success = MutableLiveData()
        val instance: FirebaseFirestore = FirebaseFirestore.getInstance()

        instance.collection(UsersCollection).document(idUser).collection(
            EducationalEstablishmentCollection).document(EducationalEstablishmentDocument)
            .set(EducationalEstablishmentDto(name,semesterPeriodStart,semesterPeriodEnd,schoolSemester,schoolDays,level,turn))
            .addOnSuccessListener {
                success.value = true
            }
            .addOnFailureListener {
                errorMessage.value = it.localizedMessage
                success.value = false
            }

        return success
    }

    fun getEducationalEstablishment(idUser:String) : MutableLiveData<EducationalEstablishmentDto> {
        establishmentDataResponse = MutableLiveData()
        val instance: FirebaseFirestore = FirebaseFirestore.getInstance()
        instance
            .collection(UsersCollection)
            .document(idUser)
            .collection(EducationalEstablishmentCollection)
            .document(EducationalEstablishmentDocument)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                establishmentDataResponse.value = documentSnapshot.toObject(EducationalEstablishmentDto::class.java)
            }
            .addOnFailureListener {
                errorMessage.value = it.localizedMessage
            }

        return establishmentDataResponse
    }
}
