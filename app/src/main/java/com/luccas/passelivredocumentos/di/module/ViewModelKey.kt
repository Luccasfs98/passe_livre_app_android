package com.luccas.passelivredocumentos.di.module

import androidx.lifecycle.ViewModel
import dagger.MapKey
import kotlin.reflect.KClass

/**
 * Created by Luccas Ferreira da Silva on 04 Outubro,2019
 */


/**
 * ViewModel Key, que serve como chave única para manter as instâncias do viewmodel na fábrica
 * */
@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@MapKey
internal annotation class ViewModelKey(val value: KClass<out ViewModel>)