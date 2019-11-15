package com.luccas.passelivredocumentos.di.builder

import com.luccas.passelivredocumentos.ui.checkingcopy.CheckingCopyFragment
import com.luccas.passelivredocumentos.ui.formaddress.FormAddressFragment
import com.luccas.passelivredocumentos.ui.formcollegeinformation.FormCollegeInformationFragment
import com.luccas.passelivredocumentos.ui.formpersonaldata.FormPersonalDataFragment
import com.luccas.passelivredocumentos.ui.formtransportdata.FormTransportDataFragment
import com.luccas.passelivredocumentos.ui.identitydocs.IdentityDocsFragment
import com.luccas.passelivredocumentos.ui.solicitationmoredetails.SolicitationMoreDetailsFragment
import com.luccas.passelivredocumentos.ui.termsandpolitics.TermsAndPoliticsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class TermsAndPoliticsModule {

    @ContributesAndroidInjector
    abstract fun termsAndPoliticsFragment(): TermsAndPoliticsFragment

}