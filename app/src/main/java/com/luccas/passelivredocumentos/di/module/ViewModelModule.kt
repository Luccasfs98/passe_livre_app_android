package com.luccas.passelivredocumentos.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.luccas.passelivredocumentos.ui.MainViewModel
import com.luccas.passelivredocumentos.ui.identitydocs.IdentityDocsViewModel
import com.luccas.passelivredocumentos.ui.login.AuthViewModel
import com.luccas.passelivredocumentos.viewmodel.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Luccas Ferreira da Silva on 04 Outubro,2019
 */


/**
 * Nos faz injetar dependências via injeção de construtor
 */

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(IdentityDocsViewModel::class)
    abstract fun identityDocsViewModel(identityDocsViewModel: IdentityDocsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun authViewModel(authViewModel: AuthViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun mainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    abstract fun bindsViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory



}
