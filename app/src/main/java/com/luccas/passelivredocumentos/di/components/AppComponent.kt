package com.luccas.passelivredocumentos.di.components

import android.app.Application
import com.luccas.passelivredocumentos.PasseLivreApplication
import com.luccas.passelivredocumentos.di.builder.ActivityBuilderModule
import com.luccas.passelivredocumentos.di.module.AppModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

/**
 * O componente principal da aplicação que inicializa todos os módulos dependentes
 * */
/**
 * Created by Luccas Ferreira da Silva on 04 Outubro,2019
 */

@Singleton
@Component(modules = [AppModule::class, AndroidInjectionModule::class, ActivityBuilderModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): AppComponent
    }

    fun inject(passeLivreApplication : PasseLivreApplication)
}