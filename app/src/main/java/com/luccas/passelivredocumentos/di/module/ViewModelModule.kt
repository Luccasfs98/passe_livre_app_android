package com.luccas.passelivredocumentos.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.luccas.passelivredocumentos.ui.MainViewModel
import com.luccas.passelivredocumentos.ui.base.UploadAndRemoveFileViewModel
import com.luccas.passelivredocumentos.ui.identitydocs.IdentityDocsViewModel
import com.luccas.passelivredocumentos.ui.login.AuthViewModel
import com.luccas.passelivredocumentos.ui.checkingcopy.CheckingCopyViewModel
import com.luccas.passelivredocumentos.ui.formaddress.FormAddressViewModel
import com.luccas.passelivredocumentos.ui.formcollegeinformation.FormCollegeInformationViewModel
import com.luccas.passelivredocumentos.ui.formpersonaldata.FormPersonalDataViewModel
import com.luccas.passelivredocumentos.ui.formtransportdata.FormTransportDataViewModel
import com.luccas.passelivredocumentos.ui.home.HomeViewModel
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
    @IntoMap
    @ViewModelKey(CheckingCopyViewModel::class)
    abstract fun checkingCopyViewModel(checkingCopyViewModel: CheckingCopyViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UploadAndRemoveFileViewModel::class)
    abstract fun uploadAndRemoveFileViewModel(uploadAndRemoveFileViewModel: UploadAndRemoveFileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FormPersonalDataViewModel::class)
    abstract fun formPersonalDataViewModel(formPersonalDataViewModel: FormPersonalDataViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FormCollegeInformationViewModel::class)
    abstract fun formCollegeInformationViewModel(formCollege: FormCollegeInformationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FormAddressViewModel::class)
    abstract fun formAddressViewModel(formAddresViewModel: FormAddressViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FormTransportDataViewModel::class)
    abstract fun formTransportDataViewModel(formTransportDataViewModel: FormTransportDataViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun homeViewModel(homeViewModel: HomeViewModel): ViewModel


    @Binds
    abstract fun bindsViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory



}
