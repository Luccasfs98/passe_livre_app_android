package com.luccas.passelivredocumentos.di.module
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


/**
 * Created by Luccas Ferreira da Silva on 04 Outubro,2019
 */

/**
 * O módulo de aplicativo que fornece instâncias amplas de vários componentes
 * */

@Module(includes = [ViewModelModule::class])
public class AppModule {


    /*  @Provides
    @Singleton
    fun provideExclusiveDatabase(application: Application): Database {
        return Room.databaseBuilder(application, Database::class.java, "express.db")
            .build()
    }
   */
   /* @Provides
    @Singleton
    fun provideTransferDao(exclusiveDatabase: Database): TransferDao {
        return exclusiveDatabase.transferDao()
    }
    */

    @Provides
    @Singleton
    fun provideFirebaseStorageInstance() : StorageReference{
        return FirebaseStorage.getInstance().reference
    }

}
