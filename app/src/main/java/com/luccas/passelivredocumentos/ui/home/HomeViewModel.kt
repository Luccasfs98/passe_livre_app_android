package com.luccas.passelivredocumentos.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.luccas.passelivredocumentos.models.DocumentsDto
import com.luccas.passelivredocumentos.models.SoliciationDto
import com.luccas.passelivredocumentos.models.User
import com.luccas.passelivredocumentos.models.UserResponse
import com.luccas.passelivredocumentos.utils.Common
import javax.inject.Inject

class HomeViewModel @Inject constructor(): ViewModel() {
    var documentsDto = MutableLiveData<SoliciationDto>()
    var error :MutableLiveData<String> = MutableLiveData()
    fun getUserDocStatus(userID:String): MutableLiveData<SoliciationDto> {
        documentsDto = MutableLiveData()
        val instance: FirebaseFirestore = FirebaseFirestore.getInstance()
        instance
            .collection(Common.SolicitationsCollection)
            .document(userID)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                documentsDto.value = documentSnapshot.toObject(SoliciationDto::class.java)
            }
            .addOnFailureListener {
                error.value = it.localizedMessage
            }

        return documentsDto
    }
}