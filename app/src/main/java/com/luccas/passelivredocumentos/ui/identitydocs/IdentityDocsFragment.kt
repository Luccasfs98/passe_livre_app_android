package com.luccas.passelivredocumentos.ui.identitydocs

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.luccas.passelivredocumentos.BuildConfig
import com.luccas.passelivredocumentos.R
import com.luccas.passelivredocumentos.ui.checkingcopy.CheckingCopyActivity
import com.luccas.passelivredocumentos.ui.base.BaseFragment
import com.luccas.passelivredocumentos.utils.Common.Companion.Front_of_Identity
import com.luccas.passelivredocumentos.utils.Common.Companion.Profile_Pic
import com.luccas.passelivredocumentos.utils.Common.Companion.Verse_of_Identity
import com.luccas.passelivredocumentos.utils.openActivity
import kotlinx.android.synthetic.main.identity_docs_fragment.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class IdentityDocsFragment : BaseFragment<IdentityDocsViewModel>(),EasyPermissions.PermissionCallbacks {

    private var count: Int = 1
    private lateinit var filePath: String
    private lateinit var fileName: String


    override val layoutRes = R.layout.identity_docs_fragment
    override fun getViewModel() = IdentityDocsViewModel::class.java

    companion object {
        fun newInstance() = IdentityDocsFragment()
        const val CODE_FOR_GALLERY = 3455
        const val CODE_FOR_CAMERA = 3453
        const val PERMANENTLY_DENIED_REQUEST_CODE = 2345
         var isProfilePicUploaded: Boolean = false
         var isIdentityFrontUploaded: Boolean = false
         var isIdentityVerseUploaded: Boolean = false
    }

    private  var file: File?=null

    private lateinit var cameraFilePath: String
    var photoBitmap: Bitmap? = null


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            activity!!.onBackPressed()
        }
        iv_profile_pic.setOnClickListener {
            if(isProfilePicUploaded){
                showBottomSheetImage()
                setImageIntoBottomSheet(Profile_Pic,R.drawable.ic_profile_pic_default)
            } else{
                filePath = Profile_Pic
                fileName = "Foto do aluno"
                selectPhoto()
            }
        }
        iv_id_front.setOnClickListener{
            if(isIdentityFrontUploaded){
                showBottomSheetImage()
                setImageIntoBottomSheet(Front_of_Identity,R.drawable.ic_add)
            } else{
                fileName = "Frente da identidade"
                filePath = Front_of_Identity
                selectPhoto()
            }
        }
        iv_id_verse.setOnClickListener{
            if(isIdentityVerseUploaded){
                showBottomSheetImage()
                setImageIntoBottomSheet(Verse_of_Identity,R.drawable.ic_add)
            } else{
                filePath = Verse_of_Identity
                fileName = "Verso da identidade"
                selectPhoto()

            }
        }

        iv_profile_pic.setOnLongClickListener {
            if (isProfilePicUploaded){
                filePath = Profile_Pic
                showDialogRemoveImage(filePath,iv_profile_pic,R.drawable.ic_profile_pic_default).observe(this, androidx.lifecycle.Observer { isProfilePicUploaded = !it })
                true
            }else{
                false
            }
        }

        iv_id_front.setOnLongClickListener {
            if (isIdentityFrontUploaded){
                filePath = Front_of_Identity
                showDialogRemoveImage(filePath,iv_id_front,R.drawable.ic_add).observe(this, androidx.lifecycle.Observer { isIdentityFrontUploaded = !it })
                true
            }else{
                false
            }
        }

        iv_id_verse.setOnLongClickListener {
            if (isIdentityVerseUploaded){
                filePath = Verse_of_Identity
                showDialogRemoveImage(filePath,iv_id_verse,R.drawable.ic_add).observe(this, androidx.lifecycle.Observer {isIdentityVerseUploaded = !it })
                true
            }else{
                false
            }
        }

        setupDocs(Profile_Pic, iv_profile_pic, R.drawable.ic_profile_pic_default)
        setupDocs(Verse_of_Identity,iv_id_verse,R.drawable.ic_add)
        setupDocs(Front_of_Identity,iv_id_front,R.drawable.ic_add)

        bt_next.setOnClickListener {
            Log.i("PROFILE PIC", isProfilePicUploaded.toString())
            Log.i("ID FRONT",isIdentityFrontUploaded.toString())
            Log.i("ID VERSE",isIdentityVerseUploaded.toString())
            if(!isProfilePicUploaded || !isIdentityFrontUploaded || !isIdentityVerseUploaded){
                Snackbar.make(root,"Você não fez o upload dos documentos ainda",Snackbar.LENGTH_SHORT).show()
            } else {
                context!!.openActivity<CheckingCopyActivity>(
                    enterAnim = R.anim.slide_from_right,
                    exitAnim = R.anim.slide_to_left
                )
            }
        }
    }



    private fun takeFromGallery() {
        val permissionsCheck = ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permissionsCheck != PackageManager.PERMISSION_GRANTED) {
            askAboutGallery()
        } else {
            afterGalleryGranted()
        }
    }

    private fun takeFromCamera() {
        val permissionCheck = ContextCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            askAboutCamera()
        } else {
            afterCameraGranted()
        }
    }
    private fun askAboutCamera() {
        EasyPermissions
            .requestPermissions(
                this,
                "A partir desse ponto a permissão de acesso à câmera é necessária.",
                CODE_FOR_CAMERA,
                Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
    }

    private fun askAboutGallery() {
        EasyPermissions
            .requestPermissions(
                this,
                "A partir deste ponto a permissão de acesso ao armazenamento interno é necessária.",
                CODE_FOR_GALLERY,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when(requestCode) {
                CODE_FOR_GALLERY -> {
                    val uri: Uri = data!!.data!!
                    try {
                        viewModel.uploadImage(fileName,filePath,uri,"image", FirebaseAuth.getInstance().currentUser!!.uid)
                        viewModel.uploadCallback.observe(this,androidx.lifecycle.Observer {
                            setPathIntoView()
                        })
                    } catch (e: Exception) {
                        Toast.makeText(context!!, "Não foi possível enviar a imagem. "+e.message, Toast.LENGTH_LONG).show()
                    }

                }
                CODE_FOR_CAMERA -> {
                    file = File(cameraFilePath)
                    try {
                        viewModel.uploadImage(
                            fileName,
                            filePath,
                            Uri.fromFile(file),
                            "image",
                            FirebaseAuth.getInstance().currentUser!!.uid
                        )
                        viewModel.uploadCallback.observe(this,androidx.lifecycle.Observer {
                            setPathIntoView()
                        })
                    } catch (e: Exception) {
                        Toast.makeText(context!!, "Não foi possível enviar a imagem. "+e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun setPathIntoView() {
        lateinit var imageView : ImageView
         var error : Int? = 0
        if (filePath == Profile_Pic){
            imageView = iv_profile_pic
            isProfilePicUploaded = true
            error = R.drawable.ic_profile_pic_default
        }
        if (filePath == Front_of_Identity){
            isIdentityFrontUploaded = true
            error = R.drawable.ic_add
            imageView = iv_id_front
        }
        if (filePath == Verse_of_Identity){
            imageView = iv_id_verse
            error = R.drawable.ic_add
            isIdentityVerseUploaded = true
        }
        setupDocs(filePath, imageView, error!!)

    }

    @SuppressLint("SimpleDateFormat")
    fun createImageFile() : File {
        val timeStamp =  SimpleDateFormat("yyyyMMdd_HHmmss").format( Date())
        val imageFileName = "PNG" + timeStamp + "_"
        //This is the directory in which the profilePicFile will be created. This is the default location of Camera photos
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        if(!storageDir.exists()){
            storageDir.mkdirs()
        }
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".png",         /* suffix */
            storageDir      /* directory */
        )

        // Save a profilePicFile: path for using again
        cameraFilePath = image.getAbsolutePath()
        return image
    }
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog
                .Builder(this)
                .setTitle("Permissões necessárias")
                .setRationale("Todas as permissões devem ser concedidas.")
                .setPositiveButton("OK")
                .setNegativeButton("Cancelar")
                .setRequestCode(PERMANENTLY_DENIED_REQUEST_CODE)
                .build()
                .show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Log.i("PERMISSÕES CONCEDIDAS", perms.toString())
        if (requestCode== CODE_FOR_CAMERA){
            afterCameraGranted()
        } else {
            afterGalleryGranted()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(CODE_FOR_CAMERA)
    private fun afterCameraGranted() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(context!!, BuildConfig.APPLICATION_ID + ".provider", createImageFile()))
        startActivityForResult(cameraIntent, CODE_FOR_CAMERA)
    }

    @AfterPermissionGranted(CODE_FOR_GALLERY)
    private fun afterGalleryGranted() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, CODE_FOR_GALLERY)
    }

    private var sheetViewRemoveImage: View? = null
    private lateinit var bottomSheetRemoveImage: BottomSheetDialog
    private fun showDialogRemoveImage(filePath:String,imageView:ImageView, imageDefault:Int) : MutableLiveData<Boolean> {
        var sucess  : MutableLiveData<Boolean>?=MutableLiveData()
        bottomSheetRemoveImage  = BottomSheetDialog(ContextThemeWrapper(context!!, R.style.DialogSlideAnim))
        sheetViewRemoveImage = layoutInflater.inflate(R.layout.bottom_sheet_remove_image, null)
        bottomSheetRemoveImage.setContentView(sheetViewRemoveImage!!)
        bottomSheetRemoveImage.show()
        bottomSheetRemoveImage.window!!.decorView.setBackgroundResource(android.R.color.transparent)
        bottomSheetRemoveImage.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        bottomSheetRemoveImage.setCancelable(true)
        sheetViewRemoveImage!!.findViewById<MaterialButton>(R.id.bt_keep).setOnClickListener {
            bottomSheetRemoveImage.dismiss()
        }
        sheetViewRemoveImage!!.findViewById<MaterialButton>(R.id.bt_remove).setOnClickListener {
            photoBitmap = null
            bottomSheetRemoveImage.dismiss()
            viewModel.removeImage(filePath,FirebaseAuth.getInstance().currentUser!!.uid).observe(this, androidx.lifecycle.Observer {
                imageView.setImageResource(imageDefault)
                sucess!!.value = true
            })
        }
    return sucess!!
    }

    private var sheetViewChoiceCameraOrGallery: View? = null
    private lateinit var bottomSheetChoiceCameraOrGallery : BottomSheetDialog

    private fun selectPhoto() {
        bottomSheetChoiceCameraOrGallery  = BottomSheetDialog(ContextThemeWrapper(context!!, R.style.DialogSlideAnim))
        sheetViewChoiceCameraOrGallery = layoutInflater.inflate(R.layout.bottom_sheet_gallery_camera, null)
        bottomSheetChoiceCameraOrGallery.setContentView(sheetViewChoiceCameraOrGallery!!)
        bottomSheetChoiceCameraOrGallery.show()
        bottomSheetChoiceCameraOrGallery.window!!.decorView.setBackgroundResource(android.R.color.transparent)
        bottomSheetChoiceCameraOrGallery.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        bottomSheetChoiceCameraOrGallery.setCancelable(true)
        sheetViewChoiceCameraOrGallery!!.findViewById<MaterialButton>(R.id.bt_camera).setOnClickListener {
            bottomSheetChoiceCameraOrGallery.dismiss()
            takeFromCamera()
        }
        sheetViewChoiceCameraOrGallery!!.findViewById<MaterialButton>(R.id.bt_galeria).setOnClickListener {
            bottomSheetChoiceCameraOrGallery.dismiss()
            takeFromGallery()
        }
    }
}
