package com.luccas.passelivredocumentos.ui.identitydocs

import android.net.Uri
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
import com.luccas.passelivredocumentos.utils.Common
import java.io.File
import java.lang.Exception
import javax.inject.Inject

class IdentityDocsViewModel  @Inject constructor(val reference : StorageReference) : ViewModel() {

    var error = MutableLiveData<String>()
    var uploadCallback = MutableLiveData<String>()
    var deleteCallback = MutableLiveData<Boolean>()
    var documentsResponse = MutableLiveData<DocumentsDto>()

    fun getDocuments(userID:String): MutableLiveData<DocumentsDto> {
        documentsResponse = MutableLiveData()
        val instance: FirebaseFirestore = FirebaseFirestore.getInstance()
        instance
            .collection(Common.UsersCollection)
            .document(userID)
            .collection(Common.DocumentsCollection)
            .document(Common.DocumentsDocument)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                documentsResponse.value = documentSnapshot.toObject(DocumentsDto::class.java)
            }
            .addOnFailureListener {
                error.value = it.localizedMessage
            }

        return documentsResponse
    }
    fun uploadImage(filePath:String, uri: Uri, userID:String) {
        uploadCallback= MutableLiveData()
        val ref = reference.child(
            "$filePath/$userID")
        val uploadTask = ref.putFile(uri)
        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    error.value = it.message
                    throw it
                }
            }
            return@Continuation ref.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                savePathIntoUser(task.result.toString(),userID,filePath)
            } else {
                try {
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
        instance.collection(Common.UsersCollection).document(userID).collection(Common.DocumentsCollection).document(Common.DocumentsDocument)
            .set(data,SetOptions.merge())
            .addOnSuccessListener {
                uploadCallback.value = result
            }
            .addOnFailureListener {
                error.value = it.message
            }
    }
    fun removeImage(filePath:String,userID:String): MutableLiveData<Boolean> {
        deleteCallback= MutableLiveData()
        val ref = reference.child(
            "$filePath/$userID")
        val uploadTask = ref.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                deleteCallback.value = task.isComplete
                //addUploadRecordToDb(downloadUri.toString())
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
}

