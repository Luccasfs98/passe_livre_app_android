package com.luccas.passelivredocumentos.ui.base

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.luccas.passelivredocumentos.R
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.dialog_progress.view.*
import javax.inject.Inject
import javax.inject.Named
abstract class BaseActivity<V : ViewModel>: AppCompatActivity(),
    HasSupportFragmentInjector {

    @Inject
    @Named("fragmentAndroidInjector")
    lateinit var fragmentAndroidInjector: DispatchingAndroidInjector<Fragment>

    lateinit var viewModel: V

    @get:LayoutRes
    abstract val layoutRes: Int

    @Inject
    @Named("viewModelFactory")
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(layoutRes)
        sharedPref = getSharedPreferences("App", 0)
        viewModel = ViewModelProvider(this, viewModelFactory).get(getViewModel())
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment>? {
        return fragmentAndroidInjector
    }
    abstract fun getViewModel(): Class<V>

    fun hideBsProgress() {
        if (bsProgress!=null){
            bsProgress!!.dismiss()
        }
    }

    private var sProgress: View? = null
    private var bsProgress: BottomSheetDialog? = null
    fun showBottomSheetProgress() {
        bsProgress  = BottomSheetDialog(ContextThemeWrapper(this, R.style.DialogSlideAnim))
        sProgress = layoutInflater.inflate(R.layout.dialog_progress, null)
        bsProgress!!.setContentView(sProgress!!)
        bsProgress!!.window!!.decorView.setBackgroundResource(android.R.color.transparent)
        bsProgress!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        bsProgress!!.setCancelable(false)
        bsProgress!!.show()
        sProgress!!.tv_message.text = "Realizando autenticação..."
    }
}

