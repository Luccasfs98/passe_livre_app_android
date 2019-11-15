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
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.luccas.passelivredocumentos.BuildConfig
import com.luccas.passelivredocumentos.R
import com.luccas.passelivredocumentos.ui.MainActivity
import com.luccas.passelivredocumentos.ui.base.BaseFragment
import com.luccas.passelivredocumentos.ui.base.UploadAndRemoveFileViewModel
import com.luccas.passelivredocumentos.ui.formcollegeinformation.FormCollegeInformationFragment
import com.luccas.passelivredocumentos.ui.identitydocs.IdentityDocsFragment
import com.luccas.passelivredocumentos.ui.identitydocs.IdentityDocsFragment.Companion.CODE_FOR_CAMERA
import com.luccas.passelivredocumentos.ui.identitydocs.IdentityDocsFragment.Companion.CODE_FOR_GALLERY
import com.luccas.passelivredocumentos.utils.Common
import com.luccas.passelivredocumentos.utils.openActivity
import kotlinx.android.synthetic.main.checking_copy_fragment.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CheckingCopyFragment : BaseFragment<CheckingCopyViewModel>() {
    companion object {
        fun newInstance() = CheckingCopyFragment()
    }

    private var isIncomeUploaded: Boolean=false
    private var isCardUploaded: Boolean=false
    private var isFrequencyUploaded: Boolean=false
    private var isRegisterCertificate: Boolean=false
    private var isAddressUploaded: Boolean=false
    private lateinit var cameraFilePath: String
    private lateinit var filePath: String

    private var file: File?=null

    private var isPDF: Boolean? = false

    private val isProuniStudent : Boolean = false
    override val layoutRes = R.layout.checking_copy_fragment

    override fun getViewModel() = CheckingCopyViewModel::class.java

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            activity!!.onBackPressed()
        }

        bt_finish.setOnClickListener {
            if (isFrequencyUploaded && isCardUploaded && isAddressUploaded && isRegisterCertificate){
                if (isProuniStudent && isIncomeUploaded){
                    Snackbar.make(root,"Obrigatório o envio de todos os documentos!",Snackbar.LENGTH_SHORT).show()
                } else {
                    askConfirmDocs()
                }
            } else {
                Snackbar.make(root,"Obrigatório o envio de todos os documentos!",Snackbar.LENGTH_SHORT).show()
            }


        }

        iv_address.setOnClickListener {
            filePath = Common.AddressComprovant
            choicePdfOrImg()
        }
        iv_income_proof.setOnClickListener {
            filePath = Common.IncomeComprovant
            choicePdfOrImg()
        }
        iv_frequency.setOnClickListener {
            filePath = Common.FrequencyComprovant
            choicePdfOrImg()
        }
        iv_add_small_card.setOnClickListener {
            filePath = Common.CardComprovant
            choicePdfOrImg()
        }
        iv_register_certificate.setOnClickListener {
            filePath = Common.Registration_Certificate
            choicePdfOrImg()
        }

        viewModel.getTransportData(sharedPref.getString("userID","")!!).observe(this, androidx.lifecycle.Observer {
            if (it!=null){
                if (it.prouniScholarshipHolder!!){
                    tv_id_verse.text = "Comprovante de bolsa ProUni"
                }
            }
        })

        viewModel.error.observe(this,androidx.lifecycle.Observer{
            showSnack(it,root)
            hideBsProgress()
            ln_main.visibility = View.VISIBLE
            progress_bar.visibility = View.GONE
        })

        viewModel.getDocuments(sharedPref.getString("userID","")!!).observe(this, androidx.lifecycle.Observer {

            if (it!=null){
                if (it.proof_of_address.isNotEmpty()){
                    iv_address.setImageDrawable(resources.getDrawable(R.drawable.ic_check))
                    isAddressUploaded = true
                }
                if (it.proof_of_income.isNotEmpty()){
                    iv_income_proof.setImageDrawable(resources.getDrawable(R.drawable.ic_check))
                    isIncomeUploaded = true
                }
                if (it.card_voucher.isNotEmpty()){
                    iv_add_small_card.setImageDrawable(resources.getDrawable(R.drawable.ic_check))
                    isCardUploaded = true
                }
                if (it.voucher_frequency.isNotEmpty()){
                    iv_frequency.setImageDrawable(resources.getDrawable(R.drawable.ic_check))
                    isFrequencyUploaded = true
                }
                if (it.registration_certificate.isNotEmpty()){
                    iv_register_certificate.setImageDrawable(resources.getDrawable(R.drawable.ic_check))
                    isRegisterCertificate = true
                }
            }
            ln_main.visibility = View.VISIBLE
            progress_bar.visibility = View.GONE
        })
    }
    private var sheetConfirmDocs: View? = null
    private lateinit var bsConfirmDocs : BottomSheetDialog
    private fun askConfirmDocs() {
        bsConfirmDocs  = BottomSheetDialog(ContextThemeWrapper(context!!, R.style.DialogSlideAnim))
        sheetConfirmDocs = layoutInflater.inflate(R.layout.bottom_sheet_confirm_docs, null)
        bsConfirmDocs.setContentView(sheetConfirmDocs!!)
        bsConfirmDocs.show()
        bsConfirmDocs.window!!.decorView.setBackgroundResource(android.R.color.transparent)
        bsConfirmDocs.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        bsConfirmDocs.setCancelable(true)
        sheetConfirmDocs!!.findViewById<MaterialButton>(R.id.bt_send).setOnClickListener {
            viewModel.confirmDocs(sharedPref.getString("userID","")!!).observe(this,androidx.lifecycle.Observer {
                activity!!.finishAffinity()
                activity!!.openActivity<MainActivity> (
                    finishWhenOpen = true,
                    exitAnim = R.anim.slide_to_left
                ){  }
            })
        }
        sheetConfirmDocs!!.findViewById<MaterialButton>(R.id.bt_review).setOnClickListener {
            bsConfirmDocs.dismiss()
            activity!!.supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_from_left,
                    R.anim.slide_to_right,
                    R.anim.slide_from_right,
                    R.anim.slide_to_left
                )
                .replace(R.id.container, FormCollegeInformationFragment.newInstance())
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commitAllowingStateLoss()
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
            isPDF = false
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
    @AfterPermissionGranted(CODE_FOR_CAMERA)
    fun afterCameraGranted() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(context!!, BuildConfig.APPLICATION_ID + ".provider", createImageFile()))
        startActivityForResult(cameraIntent, CODE_FOR_CAMERA)
    }

    @AfterPermissionGranted(CODE_FOR_GALLERY)
    fun afterGalleryGranted() {
        if (isPDF!!){
            val intent = Intent()
            intent.type = "application/pdf"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Selecione um arquivo PDF."), CODE_FOR_GALLERY)
        } else {
            val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, CODE_FOR_GALLERY)
        }

    }

    fun askAboutCamera() {
        EasyPermissions
            .requestPermissions(
                this,
                "A partir desse ponto a permissão de acesso à câmera é necessária.",
                CODE_FOR_CAMERA,
                Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
    }

    fun askAboutGallery() {
        EasyPermissions
            .requestPermissions(
                this,
                "A partir deste ponto a permissão de acesso ao armazenamento interno é necessária.",
                CODE_FOR_GALLERY,
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
        cameraFilePath = image.getAbsolutePath()
        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            showBottomSheetProgress()

            when(requestCode) {

                CODE_FOR_GALLERY -> {
                    val uri: Uri = data!!.data!!
                    if (isPDF!!){
                        try {
                            viewModel.uploadImage(filePath,uri,sharedPref.getString("userID","")!!)
                            viewModel.uploadCallback.observe(this,androidx.lifecycle.Observer {
                                updateIcon()
                                hideBsProgress()
                            })

                        } catch (e: Exception) {
                            Toast.makeText(context!!, "Não foi possível enviar a imagem. "+e.message, Toast.LENGTH_LONG).show()
                            hideBsProgress()

                        }
                    } else {
                        try {
                            viewModel.uploadImage(filePath,uri,sharedPref.getString("userID","")!!)
                            viewModel.uploadCallback.observe(this,androidx.lifecycle.Observer {
                                updateIcon()
                                hideBsProgress()
                            })

                        } catch (e: Exception) {
                            hideBsProgress()

                            Toast.makeText(context!!, "Não foi possível enviar a imagem. "+e.message, Toast.LENGTH_LONG).show()
                        }
                    }

                }
                CODE_FOR_CAMERA -> {
                    file = File(cameraFilePath)
                    try {
                        viewModel.uploadImage(filePath,Uri.fromFile(file),sharedPref.getString("userID","")!!)
                        viewModel.uploadCallback.observe(this,androidx.lifecycle.Observer {
                            updateIcon()
                            hideBsProgress()

                        })
                    } catch (e: Exception) {
                        hideBsProgress()
                        Toast.makeText(context!!, "Não foi possível enviar a imagem. "+e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun updateIcon() {
        if (filePath == Common.AddressComprovant){
            iv_address.setImageDrawable(resources.getDrawable(R.drawable.ic_check))
            isAddressUploaded = true
        }
        if (filePath == Common.FrequencyComprovant){
            iv_frequency.setImageDrawable(resources.getDrawable(R.drawable.ic_check))
            isFrequencyUploaded = true
        }
        if (filePath == Common.CardComprovant){
            iv_add_small_card.setImageDrawable(resources.getDrawable(R.drawable.ic_check))
            isCardUploaded = true
        }
        if (filePath == Common.IncomeComprovant){
            iv_income_proof.setImageDrawable(resources.getDrawable(R.drawable.ic_check))
            isIncomeUploaded = true
        }
        if (filePath == Common.Registration_Certificate){
            iv_register_certificate.setImageDrawable(resources.getDrawable(R.drawable.ic_check))
            isRegisterCertificate = true
        }
    }
}
