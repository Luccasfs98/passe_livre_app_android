package com.luccas.passelivredocumentos.ui.solicitationmoredetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.luccas.passelivredocumentos.models.DocumentsDto
import com.luccas.passelivredocumentos.models.SoliciationDto
import com.luccas.passelivredocumentos.utils.Common
import javax.inject.Inject

class SolicitationMoreDetailsViewModel @Inject constructor(): ViewModel() {
    private lateinit var documentsResponse: MutableLiveData<SoliciationDto>
    private  var error: MutableLiveData<String> = MutableLiveData()

    fun getDocuments(userID:String): MutableLiveData<SoliciationDto> {
        documentsResponse = MutableLiveData()
        val instance: FirebaseFirestore = FirebaseFirestore.getInstance()
        instance
            .collection(Common.SolicitationsCollection)
            .document(userID)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                documentsResponse.value = documentSnapshot.toObject(SoliciationDto::class.java)
            }
            .addOnFailureListener {
                error.value = it.localizedMessage
            }

        return documentsResponse
    }
}
