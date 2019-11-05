package com.luccas.passelivredocumentos.ui.identitydocs

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.firebase.storage.FirebaseStorage
import com.luccas.passelivredocumentos.BuildConfig
import com.luccas.passelivredocumentos.R
import com.luccas.passelivredocumentos.databinding.IdentityDocsFragmentBinding
import com.luccas.passelivredocumentos.ui.base.BaseFragment
import com.luccas.passelivredocumentos.utils.ImageHelper
import kotlinx.android.synthetic.main.identity_docs_fragment.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class IdentityDocsFragment : BaseFragment<IdentityDocsViewModel, IdentityDocsFragmentBinding>(),EasyPermissions.PermissionCallbacks {

    private var isProfilePicUploaded: Boolean = false
    override val layoutRes = R.layout.identity_docs_fragment
    override fun getViewModel() = IdentityDocsViewModel::class.java

    companion object {
        fun newInstance() = IdentityDocsFragment()
        const val CODE_FOR_GALLERY = 3455
        const val CODE_FOR_CAMERA = 3453
        const val PERMANENTLY_DENIED_REQUEST_CODE = 2345
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
                setImageIntoBottomSheet()
            } else{
                selectPhoto()
            }
        }

        iv_profile_pic.setOnLongClickListener {
            if (isProfilePicUploaded){
                showDialogRemoveImage()
                true
            }else{
                false
            }
        }
        setProfilePicIntoView()
    }

    fun selectPhoto(){
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("Selecione uma foto")
        val items = arrayOf("Abrir câmera", "Abrir galeria")
        builder.setItems(items){ dialog, which ->
            val choice = when(which) {
                0 -> takeFromCamera()
                1 -> takeFromGallery()
                else -> Toast.makeText(context!!, "Nenhum selecionado", Toast.LENGTH_LONG).show()
            }
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
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
            val uri: Uri

            when(requestCode) {
                CODE_FOR_GALLERY -> {
                    uri = data!!.data!!
                    this.file = File(getPath(uri))
                    var bitmap: Bitmap? = null
                    val any = try {
                            bitmap = MediaStore.Images.Media.getBitmap(context!!.contentResolver, data.data)
                            photoBitmap = bitmap
                            viewModel.uploadImage(this.file!!,sharedPref.getString("userID","")!!).observe(this, androidx.lifecycle.Observer{
                                isProfilePicUploaded = true
                                iv_profile_pic.setImageBitmap(bitmap)
                            })
                        } catch (e: Exception) {
                            Toast.makeText(context!!, "Não foi possível obter a imagem", Toast.LENGTH_LONG).show()
                    }
                }
                CODE_FOR_CAMERA -> {
                    file = File(cameraFilePath)
                    val any = try {
                        viewModel.uploadImage(this.file!!,sharedPref.getString("userID","")!!).observe(this, androidx.lifecycle.Observer{
                            isProfilePicUploaded = true
                            setProfilePicIntoView()
                        })
                    } catch (e: Exception) {
                        Toast.makeText(context!!, "Não foi possível obter a imagem", Toast.LENGTH_LONG).show()
                    }
                    photoBitmap = ImageHelper.setupBitmap(BitmapFactory.decodeFile(cameraFilePath))
                    iv_profile_pic.setImageBitmap(photoBitmap)
                }
            }
        }
    }

    private fun setProfilePicIntoView() {

        val circularProgressDrawable = CircularProgressDrawable(context!!)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        Glide.with(this)
            .load(
                FirebaseStorage.getInstance().reference
                    .child("foto_perfil/${sharedPref.getString("userID","")}"))
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .placeholder(circularProgressDrawable)
            .error(R.drawable.ic_profile_pic_default)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(p0: GlideException?, p1: Any?, p2: Target<Drawable>?, p3: Boolean): Boolean {
                    isProfilePicUploaded = false
                    return isProfilePicUploaded
                }
                override fun onResourceReady(p0: Drawable?, p1: Any?, p2: Target<Drawable>?, p3: DataSource?, p4: Boolean): Boolean {
                    isProfilePicUploaded = true
                    iv_profile_pic.setImageDrawable(p0)
                    return isProfilePicUploaded
                }
            })
            .into(iv_profile_pic)
    }

    @SuppressLint("SimpleDateFormat")
    fun createImageFile() : File {
        val timeStamp =  SimpleDateFormat("yyyyMMdd_HHmmss").format( Date())
        val imageFileName = "PNG" + timeStamp + "_"
        //This is the directory in which the file will be created. This is the default location of Camera photos
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

        // Save a file: path for using again
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
    fun getPath(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        val cursor = context!!.getContentResolver().query(uri, projection, null, null, null)
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            val column_index = cursor!!
                .getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            cursor!!.moveToFirst()
            return cursor!!.getString(column_index)
        } else
            return null
    }

    private var sheetView: View? = null
    private lateinit var bottomSheet: BottomSheetDialog

    private fun showBottomSheetImage() {
        bottomSheet  = BottomSheetDialog(ContextThemeWrapper(context!!, R.style.DialogSlideAnim))
        sheetView = layoutInflater.inflate(R.layout.bottom_image_view, null)
        bottomSheet.setContentView(sheetView!!)
        bottomSheet.window!!.decorView.setBackgroundResource(android.R.color.transparent)
        bottomSheet.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        bottomSheet.setCancelable(true)
        bottomSheet.show()

    }
    private fun setImageIntoBottomSheet(){

        val imageView = bottomSheet.findViewById<ImageView>(R.id.iv)
        Glide.with(this)
            .load(
                FirebaseStorage.getInstance().reference
                    .child("foto_perfil/${sharedPref.getString("userID","")}"))
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .error(R.drawable.ic_profile_pic_default)
            .into(imageView!!)
    }
    private var sheetViewRemoveImage: View? = null
    private lateinit var bottomSheetRemoveImage: BottomSheetDialog
    private fun showDialogRemoveImage() {

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
            viewModel.removeImage(sharedPref.getString("userID","")!!).observe(this, androidx.lifecycle.Observer {
                iv_profile_pic.setImageResource(R.drawable.ic_profile_pic_default)
                isProfilePicUploaded = false
            })
        }

    }

}
