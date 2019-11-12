package com.luccas.passelivredocumentos.di.builder

import com.luccas.passelivredocumentos.ui.formaddress.FormAddressFragment
import com.luccas.passelivredocumentos.ui.formcollegeinformation.FormCollegeInformationFragment
import com.luccas.passelivredocumentos.ui.formpersonaldata.FormPersonalDataFragment
import com.luccas.passelivredocumentos.ui.formtransportdata.FormTransportDataFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FormBuilderModule {

    @ContributesAndroidInjector
    abstract fun formPersonalDataFragment(): FormPersonalDataFragment

    @ContributesAndroidInjector
    abstract fun formAddressFragment(): FormAddressFragment

    @ContributesAndroidInjector
    abstract fun formCollegeInformation() : FormCollegeInformationFragment

    @ContributesAndroidInjector
    abstract fun formTransportDataFragment() : FormTransportDataFragment

}