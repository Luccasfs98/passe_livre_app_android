package com.luccas.passelivredocumentos.di.builder

import com.luccas.passelivredocumentos.ui.MainActivity
import com.luccas.passelivredocumentos.ui.SplashScreenActivity
import com.luccas.passelivredocumentos.ui.identitydocs.IdentityDocsActivity
import com.luccas.passelivredocumentos.ui.login.AuthActivity
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

    @ContributesAndroidInjector
    abstract fun authActivity(): AuthActivity

    @ContributesAndroidInjector(modules = [IdentityDocsFragmentBuilderModule::class])
    abstract fun homeActivity(): IdentityDocsActivity

    @ContributesAndroidInjector
    abstract fun mainActivity(): MainActivity


}
