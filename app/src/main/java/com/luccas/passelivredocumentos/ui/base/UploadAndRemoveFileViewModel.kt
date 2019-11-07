package com.luccas.passelivredocumentos.ui.base

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.File
import javax.inject.Inject

/**
/ Created by Luccas Ferreira da Silva on 06 Novembro,2019
/ Company: CroSoften Tecnologia
 **/
class UploadAndRemoveFileViewModel @Inject constructor(val reference : StorageReference) : ViewModel(){

    var uploadCallback = MutableLiveData<Boolean>()
    var deleteCallback = MutableLiveData<Boolean>()

    fun uploadImage(filePath:String, file: File, userID:String): MutableLiveData<Boolean> {
        uploadCallback= MutableLiveData()
        if(file != null){
            val ref = reference.child(
                "$filePath/$userID")
            val uploadTask = ref.putFile(Uri.fromFile(file))
            uploadTask.addOnFailureListener {
                // Handle unsuccessful uploads
            }.addOnSuccessListener {
                // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                // ...
                uploadCallback.value = true
            }
        }else{
            //Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show()
        }
        return uploadCallback
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
                Log.i("FAILURE DELETE",task.exception.toString())
            }
        }?.addOnFailureListener{
            Log.i("FAILURE DELETE",it.localizedMessage.toString())
        }
        return deleteCallback
    }

}