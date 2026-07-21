// Engineered by uncoalesced
package com.uncoalesced.impart.core.di

import android.content.Context
import com.uncoalesced.impart.core.security.CryptoManager
import com.uncoalesced.impart.core.security.KeystoreHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CryptoModule {

    @Provides
    @Singleton
    fun provideKeystoreHelper(@ApplicationContext context: Context): KeystoreHelper {
        return KeystoreHelper(context)
    }

    @Provides
    @Singleton
    fun provideCryptoManager(keystoreHelper: KeystoreHelper): CryptoManager {
        return CryptoManager(keystoreHelper)
    }
}
