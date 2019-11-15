package com.luccas.passelivredocumentos.ui.formaddress

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.luccas.passelivredocumentos.models.AddressDto
import com.luccas.passelivredocumentos.utils.Common
import javax.inject.Inject

class FormAddressViewModel @Inject constructor(): ViewModel() {
    lateinit var success : MutableLiveData<Boolean>
    lateinit var addressResponse : MutableLiveData<AddressDto>
    var errorMessage : MutableLiveData<String> = MutableLiveData()
    fun sendFormToApi(
        idUser:String,
        cep: String,
        address: String,
        complement: String,
        city: String,
        number: String,
        neighborhood: String,
        uf: String
    ): MutableLiveData<Boolean> {
        success = MutableLiveData()
        val instance: FirebaseFirestore = FirebaseFirestore.getInstance()

        instance.collection(Common.UsersCollection).document(idUser).collection(Common.AddressCollection).document(Common.AddressDocument)
            .set(AddressDto(cep,address,complement,city,number,neighborhood,uf))
            .addOnSuccessListener {
                success.value = true
            }
            .addOnFailureListener {
                errorMessage.value = it.localizedMessage
                success.value = false
            }

        return success
    }

    fun getAddress(idUser:String) : MutableLiveData<AddressDto> {
        addressResponse = MutableLiveData()
        val instance: FirebaseFirestore = FirebaseFirestore.getInstance()
        instance
            .collection(Common.UsersCollection)
            .document(idUser)
            .collection(Common.AddressCollection)
            .document(Common.AddressDocument)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                addressResponse.value = documentSnapshot.toObject(AddressDto::class.java)
            }
            .addOnFailureListener {
                errorMessage.value = it.localizedMessage
            }

        return addressResponse
    }}
