package com.luccas.passelivredocumentos.di.builder

import com.luccas.passelivredocumentos.FormAddressActivity
import com.luccas.passelivredocumentos.SolicitationMoreDetails
import com.luccas.passelivredocumentos.TermsActivity
import com.luccas.passelivredocumentos.TermsAndPoliticsActivity
import com.luccas.passelivredocumentos.ui.formpersonaldata.FormPersonalData
import com.luccas.passelivredocumentos.ui.checkingcopy.CheckingCopyActivity
import com.luccas.passelivredocumentos.ui.MainActivity
import com.luccas.passelivredocumentos.ui.SplashScreenActivity
import com.luccas.passelivredocumentos.ui.identitydocs.IdentityDocsActivity
import com.luccas.passelivredocumentos.ui.login.AuthActivity
import com.luccas.passelivredocumentos.ui.solicitationmoredetails.SolicitationMoreDetailsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * O módulo que fornece o serviço de injeção do Android para Activities.
 * */
/**
 * Created by Luccas Ferreira da Silva on 04 Outubro,2019
 */


@Module
abstract class ActivityBuilderModule {

    @ContributesAndroidInjector
    abstract fun splashScreenActivity(): SplashScreenActivity

    @ContributesAndroidInjector(modules = [TermsAndPoliticsModule::class])
    abstract fun authActivity(): AuthActivity

    @ContributesAndroidInjector(modules = [FormBuilderModule::class])
    abstract fun identityDocs(): IdentityDocsActivity

    @ContributesAndroidInjector(modules = [MainActivityFragmentBuilder::class])
    abstract fun mainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [FormBuilderModule::class])
    abstract fun checkingCopyActivity(): CheckingCopyActivity

    @ContributesAndroidInjector(modules = [FormBuilderModule::class])
    abstract fun formPersonalData(): FormPersonalData

    @ContributesAndroidInjector(modules = [FormBuilderModule::class])
    abstract fun formAddress(): FormAddressActivity

    @ContributesAndroidInjector(modules = [SolicitationMoreDetailsModule::class])
    abstract fun SolicitationMoreDetailsFragment(): SolicitationMoreDetails

    @ContributesAndroidInjector(modules = [TermsModule::class])
    abstract fun termsActivity(): TermsActivity
    @ContributesAndroidInjector(modules = [TermsAndPoliticsModule::class])
    abstract fun terms(): TermsAndPoliticsActivity

}
