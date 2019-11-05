package com.luccas.passelivredocumentos.ui.base

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.Nullable
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by Gustavo de Jesus Gomes on 17 Agosto,2019
 * Company: CroSoften Tecnologia
 */

public abstract class BaseFragment<V : ViewModel, D : ViewDataBinding> : Fragment() {

    @Inject
    @Named("viewModelFactory")
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var viewModel: V

    lateinit var dataBinding: D

    lateinit var sharedPref: SharedPreferences


    @get:LayoutRes
    abstract val layoutRes: Int

    abstract fun getViewModel(): Class<V>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        sharedPref = context!!.getSharedPreferences("App", 0)
        viewModel = ViewModelProvider(this, viewModelFactory!!).get(getViewModel())
    }

    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        return dataBinding.root
    }

}
