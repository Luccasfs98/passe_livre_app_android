package com.luccas.passelivredocumentos.ui.checkingcopy

import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.luccas.passelivredocumentos.models.DocumentsDto
import com.luccas.passelivredocumentos.models.DocumentsDto2
import com.luccas.passelivredocumentos.models.FormTransportDto
import com.luccas.passelivredocumentos.utils.Common
import com.luccas.passelivredocumentos.utils.Common.Companion.DocumentsCollection
import com.luccas.passelivredocumentos.utils.Common.Companion.DocumentsDocument
import com.luccas.passelivredocumentos.utils.Common.Companion.UsersCollection
import com.luccas.passelivredocumentos.utils.Common.Companion.alreadyHavePasseLivre
import com.luccas.passelivredocumentos.utils.Common.Companion.card_voucher
import com.luccas.passelivredocumentos.utils.Common.Companion.cep
import com.luccas.passelivredocumentos.utils.Common.Companion.city
import com.luccas.passelivredocumentos.utils.Common.Companion.collegeName
import com.luccas.passelivredocumentos.utils.Common.Companion.complement
import com.luccas.passelivredocumentos.utils.Common.Companion.course
import com.luccas.passelivredocumentos.utils.Common.Companion.cpf
import com.luccas.passelivredocumentos.utils.Common.Companion.dateBirthday
import com.luccas.passelivredocumentos.utils.Common.Companion.email
import com.luccas.passelivredocumentos.utils.Common.Companion.establishment
import com.luccas.passelivredocumentos.utils.Common.Companion.front_of_identity
import com.luccas.passelivredocumentos.utils.Common.Companion.fullname
import com.luccas.passelivredocumentos.utils.Common.Companion.homeNumber
import com.luccas.passelivredocumentos.utils.Common.Companion.id
import com.luccas.passelivredocumentos.utils.Common.Companion.identity_verse
import com.luccas.passelivredocumentos.utils.Common.Companion.level
import com.luccas.passelivredocumentos.utils.Common.Companion.line
import com.luccas.passelivredocumentos.utils.Common.Companion.nameFather
import com.luccas.passelivredocumentos.utils.Common.Companion.nameMother
import com.luccas.passelivredocumentos.utils.Common.Companion.neighborhood
import com.luccas.passelivredocumentos.utils.Common.Companion.pendente
import com.luccas.passelivredocumentos.utils.Common.Companion.phone
import com.luccas.passelivredocumentos.utils.Common.Companion.proof_of_address
import com.luccas.passelivredocumentos.utils.Common.Companion.proof_of_income
import com.luccas.passelivredocumentos.utils.Common.Companion.prouniScholarshipHolder
import com.luccas.passelivredocumentos.utils.Common.Companion.registration_certificate
import com.luccas.passelivredocumentos.utils.Common.Companion.schoolDays
import com.luccas.passelivredocumentos.utils.Common.Companion.schoolSemester
import com.luccas.passelivredocumentos.utils.Common.Companion.semesterPeriodEnd
import com.luccas.passelivredocumentos.utils.Common.Companion.semesterPeriodStart
import com.luccas.passelivredocumentos.utils.Common.Companion.sex
import com.luccas.passelivredocumentos.utils.Common.Companion.street
import com.luccas.passelivredocumentos.utils.Common.Companion.transportName
import com.luccas.passelivredocumentos.utils.Common.Companion.turn
import com.luccas.passelivredocumentos.utils.Common.Companion.type
import com.luccas.passelivredocumentos.utils.Common.Companion.uf
import com.luccas.passelivredocumentos.utils.Common.Companion.useIntegration
import com.luccas.passelivredocumentos.utils.Common.Companion.voucher_frequency
import java.lang.Exception
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class CheckingCopyViewModel @Inject constructor(val reference: StorageReference) : ViewModel() {
    var docsConfirmed = MutableLiveData<Boolean>()
    var documentsResponse = MutableLiveData<List<DocumentsDto2>>()
    lateinit var transportData : MutableLiveData<FormTransportDto>
    var error = MutableLiveData<String>()
    var uploadCallback = MutableLiveData<Boolean>()
    var deleteCallback = MutableLiveData<Boolean>()

    fun uploadImage(
        fileName : String,
        filePath: String,
        uri: Uri,
        userID: String,
        typeFile: String
    ) {
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
                savePathIntoUser(fileName,task.result.toString(),userID,filePath,typeFile)
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

    private fun savePathIntoUser(
        fileName:String,
        result: String?,
        userID: String,
        filePath: String,
        typeFile: String
    ) {
            val instance: FirebaseFirestore = FirebaseFirestore.getInstance()
            val data = hashMapOf(
                "name" to fileName,
                "path" to result,
                "type" to typeFile
            )

            instance.collection(UsersCollection).document(userID).collection(Common.DocumentsCollection).document(filePath)
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

    fun getDocuments(userID:String): MutableLiveData<List<DocumentsDto2>> {
        documentsResponse = MutableLiveData()
        val instance: FirebaseFirestore = FirebaseFirestore.getInstance()
        instance
            .collection(UsersCollection)
            .document(userID)
            .collection(DocumentsCollection)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val list : ArrayList<DocumentsDto2> = ArrayList()
                for (d in documentSnapshot){
                    list.add(d.toObject(DocumentsDto2::class.java))
                }
                documentsResponse.value = list
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

    fun confirmDocs(idUser: String, sharedPref: SharedPreferences) :MutableLiveData<Boolean> {
        val fullname = sharedPref.getString(fullname, "")
        val alreadyHavePasseLivre = sharedPref.getBoolean(alreadyHavePasseLivre, false)
        val card_voucher = sharedPref.getString(card_voucher, "")
        val cep = sharedPref.getString(cep, "")
        val city = sharedPref.getString(city, "")
        val complement = sharedPref.getString(complement, "")
        val course = sharedPref.getString(course, "")
        val cpf = sharedPref.getString(cpf, "")
        val createdAt = Date().time
        val dateBirthday = sharedPref.getString(dateBirthday, "")
        val email = FirebaseAuth.getInstance().currentUser!!.email
        val establishment = sharedPref.getString(establishment, "")
        val front_of_identity = sharedPref.getString(front_of_identity, "")
        val id = sharedPref.getString("userID","")
        val level = sharedPref.getString(level, "")
        val line = sharedPref.getString(line, "")
        val collegeName = sharedPref.getString(collegeName, "")
        val homeNumber = sharedPref.getString(homeNumber, "")
        val nameFather = sharedPref.getString(nameFather, "")
        val nameMother = sharedPref.getString(nameMother, "")
        val neighborhood = sharedPref.getString(neighborhood, "")
        val phone = sharedPref.getString(phone, "")
        val schoolDays : List<String>
        if (sharedPref.getStringSet(Common.schoolDays, null)!=null){
             schoolDays = sharedPref.getStringSet(Common.schoolDays, null)!!.toList()
        } else {
            schoolDays = ArrayList()
        }
        val schoolSemester = sharedPref.getString(schoolSemester, "")
        val semesterPeriodEnd = sharedPref.getString(semesterPeriodEnd, "")
        val semesterPeriodStart = sharedPref.getString(semesterPeriodStart, "")
        val sex = sharedPref.getString(sex, "")
        val street = sharedPref.getString(street, "")
        val transportName = sharedPref.getString(transportName, "")
        val turn :List<String>
        if (sharedPref.getStringSet(Common.turn, null)!=null){
            turn = sharedPref.getStringSet(Common.turn, null)!!.toList()
        } else {
            turn = ArrayList()
        }

        val type = sharedPref.getString(type, "")
        val uf = sharedPref.getString(uf, "")


        val useIntegration = sharedPref.getBoolean(useIntegration, false)

        val instance: FirebaseFirestore = FirebaseFirestore.getInstance()
        val data = hashMapOf(
            Common.StatusDocs to pendente,
            Common.Description to "Enviamos sua documentação para a entidade responsável, seu status será alterado assim que os documentos forem cadastrados na Metroplan.",
            Common.Reason to "",
            Common.fullname to fullname,
            Common.alreadyHavePasseLivre to alreadyHavePasseLivre,
            Common.card_voucher to card_voucher,
            Common.cep to cep,
            Common.city to city,
            Common.complement to complement,
            Common.course to course,
            Common.cpf to cpf,
            Common.createdAt to createdAt,
            Common.dateBirthday to dateBirthday,
            Common.email to email,
            Common.establishment to establishment,
            Common.front_of_identity to front_of_identity,
            Common.id to id,
            Common.level to level,
            Common.line to line,
            Common.collegeName to collegeName,
            Common.homeNumber to homeNumber,
            Common.nameFather to nameFather,
            Common.nameMother to nameMother,
            Common.neighborhood to neighborhood,
            Common.phone to phone,
            Common.schoolDays to schoolDays,
            Common.schoolSemester to schoolSemester,
            Common.semesterPeriodEnd to semesterPeriodEnd,
            Common.semesterPeriodStart to semesterPeriodStart,
            Common.sex to sex,
            Common.street to street,
            Common.transportName to transportName,
            Common.turn to turn,
            Common.type to type,
            Common.uf to uf,
            Common.useIntegration to useIntegration
        )


        instance
            .collection(UsersCollection)
            .document(idUser)
            .collection(DocumentsCollection)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                for (d in documentSnapshot.documents){
                    val hash = hashMapOf<String, Any>(
                        "name" to d["name"].toString(),
                        "path" to d["path"].toString(),
                        "type" to d["type"].toString()
                    )
                    instance.collection(Common.SolicitationsCollection)
                        .document(idUser)
                        .collection("files")
                        .document(d["name"].toString())
                        .set(hash)
                        .addOnSuccessListener {
                        }
                }
                instance.collection(Common.SolicitationsCollection)
                    .document(idUser)
                    .set(data, SetOptions.merge())
                    .addOnSuccessListener {
                        docsConfirmed.value = true
                    }
                    .addOnFailureListener {
                        error.value = it.message
                    }


            }
            .addOnFailureListener {
                error.value = it.localizedMessage
            }
        return docsConfirmed
    }
}

