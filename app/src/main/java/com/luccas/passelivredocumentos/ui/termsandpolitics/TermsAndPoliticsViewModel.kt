package com.luccas.passelivredocumentos.ui.termsandpolitics

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.luccas.passelivredocumentos.utils.Common
import javax.inject.Inject

class TermsAndPoliticsViewModel @Inject constructor(): ViewModel() {
    var typeResponse = MutableLiveData<String>()
    var error = MutableLiveData<String>()

    fun getTerms(type : String) : MutableLiveData<String> {

        val instance: FirebaseFirestore = FirebaseFirestore.getInstance()
        instance
            .collection(Common.terms_and_politics)
            .document(Common.terms_and_politics_document)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                typeResponse.value = documentSnapshot[type].toString()
            }
            .addOnFailureListener {
                error.value = it.localizedMessage
            }
        return typeResponse
    }
}
