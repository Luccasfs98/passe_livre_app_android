package com.luccas.passelivredocumentos.ui.base

import android.content.SharedPreferences
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by Gustavo de Jesus Gomes on 17 Agosto,2019
 * Company: CroSoften Tecnologia
 */

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

}

