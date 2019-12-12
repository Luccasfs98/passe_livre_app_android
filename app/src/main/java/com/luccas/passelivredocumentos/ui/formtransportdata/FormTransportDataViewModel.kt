package com.luccas.passelivredocumentos.ui.formtransportdata

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.luccas.passelivredocumentos.models.FormTransportDto
import com.luccas.passelivredocumentos.utils.Common
import javax.inject.Inject

class FormTransportDataViewModel @Inject constructor(): ViewModel() {
    lateinit var success : MutableLiveData<Boolean>
    lateinit var transportData : MutableLiveData<FormTransportDto>
    var errorMessage : MutableLiveData<String> = MutableLiveData()

    fun sendFormToApi(
        idUser: String,
        transportName: String,
        line: String,
        alreadyHavePasseLivre: Boolean?,
        useIntegration: Boolean?,
        prouniScholarshipHolder: Boolean?
        ) : MutableLiveData<Boolean> {
        success = MutableLiveData()
        val instance: FirebaseFirestore = FirebaseFirestore.getInstance()

        instance.collection(Common.UsersCollection).document(idUser).collection(Common.FormTransportCollection).document(
            Common.FormTransportDocument)
            .set(FormTransportDto(transportName,line,alreadyHavePasseLivre,useIntegration,prouniScholarshipHolder))
            .addOnSuccessListener { success.value = true }
            .addOnFailureListener { errorMessage.value = it.localizedMessage
                success.value = false
            }
        return success
    }

    fun getTransportData(idUser:String) : MutableLiveData<FormTransportDto> {
        transportData = MutableLiveData()
        val instance: FirebaseFirestore = FirebaseFirestore.getInstance()
        instance
            .collection(Common.UsersCollection)
            .document(idUser)
            .collection(Common.FormTransportCollection)
            .document(Common.FormTransportDocument)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                transportData.value = documentSnapshot.toObject(FormTransportDto::class.java)
            }
            .addOnFailureListener {
                errorMessage.value = it.localizedMessage
            }
        return transportData
    }

    fun getTransports() : MutableLiveData<MutableList<DocumentSnapshot>> {
        var documents = MutableLiveData<MutableList<DocumentSnapshot>>()
        val instance: FirebaseFirestore = FirebaseFirestore.getInstance()
        instance
            .collection(Common.TransportsCollection)
            .get()
            .addOnSuccessListener {
                documents.value = it.documents
            }
            .addOnFailureListener {
                errorMessage.value = it.localizedMessage
            }
        return documents
    }
}
