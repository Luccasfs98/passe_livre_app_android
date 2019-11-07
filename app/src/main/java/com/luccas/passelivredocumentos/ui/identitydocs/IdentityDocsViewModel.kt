package com.luccas.passelivredocumentos.ui.identitydocs

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.File
import javax.inject.Inject

class IdentityDocsViewModel  @Inject constructor(val reference : StorageReference) : ViewModel() {

     var uploadCallback = MutableLiveData<Boolean>()
     fun uploadImage(filePath:String,file: File,userID:String):MutableLiveData<Boolean>{
        if(file != null){
            val ref = reference.child(
                "$filePath/$userID")
            val uploadTask = ref.putFile(Uri.fromFile(file))

            val urlTask = uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl
            })?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    uploadCallback.value = task.isComplete
                    //addUploadRecordToDb(downloadUri.toString())
                } else {
                    Log.i("FAILURE UPLOAD",task.exception.toString())
                }
            }?.addOnFailureListener{
                Log.i("FAILURE UPLOAD",it.localizedMessage.toString())
            }
        }else{
            //Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show()
        }
         return uploadCallback
    }
     fun removeImage(filePath:String,userID:String):MutableLiveData<Boolean>{
             val ref = reference.child(
                 "$filePath/$userID")
             val uploadTask = ref.delete().addOnCompleteListener { task ->
                 if (task.isSuccessful) {
                     val downloadUri = task.result
                     uploadCallback.value = task.isComplete
                     //addUploadRecordToDb(downloadUri.toString())
                 } else {
                     Log.i("FAILURE DELETE",task.exception.toString())
                 }
             }?.addOnFailureListener{
                 Log.i("FAILURE DELETE",it.localizedMessage.toString())
             }
         return uploadCallback
     }
}
