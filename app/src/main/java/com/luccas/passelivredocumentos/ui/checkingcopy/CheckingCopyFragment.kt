package com.luccas.passelivredocumentos.ui.checkingcopy
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.luccas.passelivredocumentos.BuildConfig
import com.luccas.passelivredocumentos.R
import com.luccas.passelivredocumentos.ui.MainActivity
import com.luccas.passelivredocumentos.ui.base.BaseFragment
import com.luccas.passelivredocumentos.ui.base.UploadAndRemoveFileViewModel
import com.luccas.passelivredocumentos.ui.identitydocs.IdentityDocsFragment
import com.luccas.passelivredocumentos.utils.openActivity
import kotlinx.android.synthetic.main.checking_copy_fragment.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CheckingCopyFragment : BaseFragment<UploadAndRemoveFileViewModel>() {
    companion object {
        fun newInstance() = CheckingCopyFragment()
    }
    private  var file: File?=null

    private var isPDF: Boolean? = false

    private val PDF: Int ? = 0

    private val ADDRESS_CODE: Int  = 1
    private val FREQUENCY_CODE: Int  = 2
    private val SMAL_CARD_CODE: Int  = 3
    private val PROOF_INCOME: Int  = 4

    private var REQUEST_CODE:Int = 0

    override val layoutRes = R.layout.checking_copy_fragment

    override fun getViewModel() = UploadAndRemoveFileViewModel::class.java

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            activity!!.onBackPressed()
        }

        bt_next.setOnClickListener {

            sharedPref.edit().putBoolean("sendedDocs",true).apply()
            activity!!.finishAffinity()
            activity!!.openActivity<MainActivity> (
                finishWhenOpen = true,
                enterAnim = R.anim.anim_fade_out,
                exitAnim = R.anim.anim_fade_in
            ){  }
        }

        iv_address.setOnClickListener {
            REQUEST_CODE = ADDRESS_CODE
            choicePdfOrImg()
        }
    }

    private var sheetPdfOrImage: View? = null
    private lateinit var bsPdfOrImage : BottomSheetDialog

    private fun choicePdfOrImg() {
        bsPdfOrImage  = BottomSheetDialog(ContextThemeWrapper(context!!, R.style.DialogSlideAnim))
        sheetPdfOrImage = layoutInflater.inflate(R.layout.bottom_sheet_pdf_image, null)
        bsPdfOrImage.setContentView(sheetPdfOrImage!!)
        bsPdfOrImage.show()
        bsPdfOrImage.window!!.decorView.setBackgroundResource(android.R.color.transparent)
        bsPdfOrImage.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        bsPdfOrImage.setCancelable(true)
        sheetPdfOrImage!!.findViewById<MaterialButton>(R.id.bt_pdf).setOnClickListener {
            bsPdfOrImage.dismiss()
            isPDF = true
            askAboutGallery()

        }
        sheetPdfOrImage!!.findViewById<MaterialButton>(R.id.bt_imagem).setOnClickListener {
            bsPdfOrImage.dismiss()
            selectPhoto()
        }
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

    fun takeFromGallery() {
        val permissionsCheck = ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permissionsCheck != PackageManager.PERMISSION_GRANTED) {
            askAboutGallery()
        } else {
            isPDF = false
            afterGalleryGranted()
        }
    }

    fun takeFromCamera() {
        val permissionCheck = ContextCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            askAboutCamera()
        } else {
            afterCameraGranted()
        }
    }
    @AfterPermissionGranted(IdentityDocsFragment.CODE_FOR_CAMERA)
    fun afterCameraGranted() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(context!!, BuildConfig.APPLICATION_ID + ".provider", createImageFile()))
        startActivityForResult(cameraIntent, REQUEST_CODE)
    }

    @AfterPermissionGranted(IdentityDocsFragment.CODE_FOR_GALLERY)
    fun afterGalleryGranted() {
        if (isPDF!!){
            val intent = Intent()
            intent.type = "application/pdf"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select PDF"), REQUEST_CODE)
        } else {
            val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, REQUEST_CODE)
        }

    }

    fun askAboutCamera() {
        EasyPermissions
            .requestPermissions(
                this,
                "A partir desse ponto a permissão de acesso à câmera é necessária.",
                IdentityDocsFragment.CODE_FOR_CAMERA,
                Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
    }

    fun askAboutGallery() {
        EasyPermissions
            .requestPermissions(
                this,
                "A partir deste ponto a permissão de acesso ao armazenamento interno é necessária.",
                IdentityDocsFragment.CODE_FOR_GALLERY,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
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
        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when(requestCode) {

            }
        }
    }
}
