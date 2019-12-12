package com.luccas.passelivredocumentos.ui.base

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.Nullable
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.core.FirestoreClient
import com.google.firebase.storage.FirebaseStorage
import com.luccas.passelivredocumentos.BuildConfig
import com.luccas.passelivredocumentos.R
import com.luccas.passelivredocumentos.models.DocumentsDto
import com.luccas.passelivredocumentos.models.DocumentsDto2
import com.luccas.passelivredocumentos.ui.identitydocs.IdentityDocsFragment
import com.luccas.passelivredocumentos.ui.login.AuthActivity
import com.luccas.passelivredocumentos.utils.Common
import com.luccas.passelivredocumentos.utils.openActivity
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.bottom_image_view.view.*
import kotlinx.android.synthetic.main.dialog_progress.view.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Named


abstract class BaseFragment<V : ViewModel> : Fragment() {

    @Inject
    @Named("viewModelFactory")
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var viewModel: V
    lateinit var sharedPref: SharedPreferences
    lateinit var user : FirebaseUser
    lateinit var storage : FirebaseStorage
    lateinit var firestore : FirebaseFirestore
    @get:LayoutRes
    abstract val layoutRes: Int

    abstract fun getViewModel(): Class<V>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        sharedPref = context!!.getSharedPreferences("App", 0)
        viewModel = ViewModelProvider(this, viewModelFactory!!).get(getViewModel())

        if (FirebaseAuth.getInstance().currentUser!=null){
            user = FirebaseAuth.getInstance().currentUser!!
        } else {
            context!!.openActivity<AuthActivity>(finishWhenOpen = true) {  }
        }
        storage = FirebaseStorage.getInstance()
        firestore  = FirebaseFirestore.getInstance()
    }

    fun hideBsProgress() {
        if (bsProgress!=null){
            bsProgress!!.dismiss()
        }
    }

    private var sProgress: View? = null
    private var bsProgress: BottomSheetDialog? = null
    fun showBottomSheetProgress() {
        bsProgress  = BottomSheetDialog(ContextThemeWrapper(context!!, R.style.DialogSlideAnim))
        sProgress = layoutInflater.inflate(R.layout.dialog_progress, null)
        bsProgress!!.setContentView(sProgress!!)
        bsProgress!!.window!!.decorView.setBackgroundResource(android.R.color.transparent)
        bsProgress!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        bsProgress!!.setCancelable(false)
        bsProgress!!.show()
        sProgress!!.tv_message.text = "Estamos enviando suas informações..."
    }

    private var sheetView: View? = null
    private lateinit var bottomSheet: BottomSheetDialog

    fun showBottomSheetImage() {
        bottomSheet  = BottomSheetDialog(ContextThemeWrapper(context!!, R.style.DialogSlideAnim))
        sheetView = layoutInflater.inflate(R.layout.bottom_image_view, null)
        bottomSheet.setContentView(sheetView!!)
        bottomSheet.window!!.decorView.setBackgroundResource(android.R.color.transparent)
        bottomSheet.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        bottomSheet.setCancelable(true)
        bottomSheet.show()

    }
    fun setImageIntoBottomSheet(path:String,error:Int){
        sheetView!!.progress_bar.visibility = View.VISIBLE
        Glide.with(this)
            .load(
                FirebaseStorage.getInstance().reference
                    .child("$path/${FirebaseAuth.getInstance().currentUser!!.uid}"))
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(p0: GlideException?, p1: Any?, p2: Target<Drawable>?, p3: Boolean): Boolean {
                    sheetView!!.progress_bar.visibility = View.GONE
                    //sheetView!!.findViewById<ImageView>(R.id.iv)!!.visibility = View.VISIBLE
                    Toast.makeText(context!!,p0.toString(),Toast.LENGTH_LONG).show()
                    bottomSheet.dismiss()
                    return false
                }
                override fun onResourceReady(p0: Drawable?, p1: Any?, p2: Target<Drawable>?, p3: DataSource?, p4: Boolean): Boolean {
                    sheetView!!.progress_bar!!.visibility = View.GONE
                    sheetView!!.iv.setImageDrawable(p0)
                    return false
                }
            })
            .into( sheetView!!.iv)
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

    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutRes,container,false)
    }
    fun setupDocs(
        path: String,
        imageView: ImageView?,
        errorImage: Int
    )  {
        val circularProgressDrawable = CircularProgressDrawable(context!!)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.setTint(resources.getColor(R.color.colorWhite))
        circularProgressDrawable.start()

        val instance: FirebaseFirestore = FirebaseFirestore.getInstance()
        instance
            .collection(Common.UsersCollection)
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection(Common.DocumentsCollection)
            .document(path)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                var url = ""
                if (documentSnapshot.toObject(DocumentsDto2::class.java)!=null){
                    if (path== Common.Profile_Pic){
                        url =documentSnapshot.toObject(DocumentsDto2::class.java)!!.path
                        sharedPref.edit().putString(path,url).apply()
                    }
                    if (path== Common.Front_of_Identity){
                        url =documentSnapshot.toObject(DocumentsDto2::class.java)!!.path
                        if (url!=null)
                            sharedPref.edit().putString(path,url).apply()
                    }
                    if (path== Common.Verse_of_Identity){
                        url =documentSnapshot.toObject(DocumentsDto2::class.java)!!.path
                        if (url!=null)
                            sharedPref.edit().putString(path,url).apply()
                    }
                    Glide.with(this)
                        .load(url)
                        .error(errorImage)
                        .placeholder(circularProgressDrawable)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(p0: GlideException?, p1: Any?, p2: Target<Drawable>?, p3: Boolean): Boolean {
                                imageView!!.setImageResource(errorImage)
                                Toast.makeText(context!!,p0.toString(),Toast.LENGTH_LONG).show()
                                return false
                            }
                            override fun onResourceReady(p0: Drawable?, p1: Any?, p2: Target<Drawable>?, p3: DataSource?, p4: Boolean): Boolean {
                                if (path== Common.Profile_Pic){
                                    IdentityDocsFragment.isProfilePicUploaded = true
                                }
                                if (path== Common.Front_of_Identity){
                                    IdentityDocsFragment.isIdentityFrontUploaded = true
                                }
                                if (path== Common.Verse_of_Identity){
                                    IdentityDocsFragment.isIdentityVerseUploaded = true
                                }
                                return false
                            }
                        })
                        .into(imageView!!)
                }
            }
            .addOnFailureListener {
                Toast.makeText(context!!,it.message,Toast.LENGTH_LONG).show()
            }

    }

   fun showSnack( it:String, root : View){
        Snackbar.make(root,it,Snackbar.LENGTH_SHORT).show()
    }


}
