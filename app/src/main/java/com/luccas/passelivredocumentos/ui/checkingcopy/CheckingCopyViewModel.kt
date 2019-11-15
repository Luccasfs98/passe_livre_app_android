package com.luccas.passelivredocumentos.ui.checkingcopy

import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.luccas.passelivredocumentos.models.DocumentsDto
import com.luccas.passelivredocumentos.models.FormTransportDto
import com.luccas.passelivredocumentos.models.PersonalDataDto
import com.luccas.passelivredocumentos.utils.Common
import com.luccas.passelivredocumentos.utils.Common.Companion.DocumentsCollection
import com.luccas.passelivredocumentos.utils.Common.Companion.DocumentsDocument
import com.luccas.passelivredocumentos.utils.Common.Companion.PersonalDataCollection
import com.luccas.passelivredocumentos.utils.Common.Companion.PersonalDataDocument
import com.luccas.passelivredocumentos.utils.Common.Companion.UsersCollection
import com.luccas.passelivredocumentos.utils.Common.Companion.pendente
import java.io.File
import java.lang.Exception
import javax.inject.Inject

class CheckingCopyViewModel @Inject constructor(val reference: StorageReference) : ViewModel() {
    var docsConfirmed = MutableLiveData<Boolean>()
    var documentsResponse = MutableLiveData<DocumentsDto>()
    lateinit var transportData : MutableLiveData<FormTransportDto>
    var error = MutableLiveData<String>()
    var uploadCallback = MutableLiveData<Boolean>()
    var deleteCallback = MutableLiveData<Boolean>()

    fun uploadImage(filePath:String, uri: Uri, userID:String) {
        uploadCallback= MutableLiveData()
        val ref = reference.child(
            "$filePath/$userID")
        val uploadTask = ref.putFile(uri)
        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation ref.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                savePathIntoUser(task.result.toString(),userID,filePath)
            } else {
                try {
                    uploadCallback.value = false
                    error.value = task.exception!!.message
                } catch (e : Exception){
                    error.value = e.message
                }
            }
        }
    }

    private fun savePathIntoUser(result: String?, userID: String,fileName:String) {
            val instance: FirebaseFirestore = FirebaseFirestore.getInstance()
             val data = hashMapOf(fileName to result)

            instance.collection(UsersCollection).document(userID).collection(Common.DocumentsCollection).document(Common.DocumentsDocument)
                .set(data, SetOptions.merge())
                .addOnSuccessListener {
                    uploadCallback.value = true
                }
                .addOnFailureListener {
                    uploadCallback.value = false
                    error.value = it.message
                }
    }
    fun removeImage(filePath:String,userID:String): MutableLiveData<Boolean> {
        deleteCallback= MutableLiveData()
        val ref = reference.child(
            "$filePath/$userID")
        ref.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result
                deleteCallback.value = task.isComplete
            } else {
                error.value = task.exception!!.message
                Log.i("FAILURE DELETE",task.exception.toString())
            }
        }?.addOnFailureListener{
            error.value = it.message
            Log.i("FAILURE DELETE",it.localizedMessage.toString())
        }
        return deleteCallback
    }

    fun getDocuments(userID:String): MutableLiveData<DocumentsDto> {
        documentsResponse = MutableLiveData()
        val instance: FirebaseFirestore = FirebaseFirestore.getInstance()
        instance
            .collection(UsersCollection)
            .document(userID)
            .collection(DocumentsCollection)
            .document(DocumentsDocument)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                documentsResponse.value = documentSnapshot.toObject(DocumentsDto::class.java)
            }
            .addOnFailureListener {
                error.value = it.localizedMessage
            }

        return documentsResponse
    }

    fun getTransportData(idUser:String) : MutableLiveData<FormTransportDto> {
        transportData = MutableLiveData()
        val instance: FirebaseFirestore = FirebaseFirestore.getInstance()
        instance
            .collection(UsersCollection)
            .document(idUser)
            .collection(Common.FormTransportCollection)
            .document(Common.FormTransportDocument)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                transportData.value = documentSnapshot.toObject(FormTransportDto::class.java)
            }
            .addOnFailureListener {
                error.value = it.localizedMessage
            }
        return transportData
    }

    fun confirmDocs(idUser: String) :MutableLiveData<Boolean> {
        val instance: FirebaseFirestore = FirebaseFirestore.getInstance()
        val data = hashMapOf(
            Common.StatusDocs to pendente,
            Common.Description to "Enviamos sua documentação para a entidade responsável, seu status será alterado assim que os documentos forem cadastrados na Metroplan.",
            Common.IsDocumentsConfirmed to true,
            Common.Reason to ""
        )
        instance.collection(UsersCollection).document(idUser)
            .collection(DocumentsCollection)
            .document(DocumentsDocument)
            .set(data, SetOptions.merge())
            .addOnSuccessListener {
                docsConfirmed.value = true
            }
            .addOnFailureListener {
                error.value = it.message
            }

        return docsConfirmed
    }
}

