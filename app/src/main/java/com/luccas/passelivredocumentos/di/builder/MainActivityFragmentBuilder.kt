package com.luccas.passelivredocumentos.di.builder

import com.luccas.passelivredocumentos.ui.checkingcopy.CheckingCopyFragment
import com.luccas.passelivredocumentos.ui.formaddress.FormAddressFragment
import com.luccas.passelivredocumentos.ui.formpersonaldata.FormPersonalDataFragment
import com.luccas.passelivredocumentos.ui.home.HomeFragment
import com.luccas.passelivredocumentos.ui.identitydocs.IdentityDocsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
/ Created by Luccas Ferreira da Silva on 03 Novembro,2019
/ Company: CroSoften Tecnologia
 **/
@Module
abstract class MainActivityFragmentBuilder {

    @ContributesAndroidInjector
    abstract fun homeFragment(): HomeFragment

}