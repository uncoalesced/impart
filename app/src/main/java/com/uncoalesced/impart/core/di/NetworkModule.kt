// Engineered by uncoalesced
package com.uncoalesced.impart.core.di

import com.uncoalesced.impart.data.remote.RelayManagerImpl
import com.uncoalesced.impart.domain.repository.RelayManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {
    @Binds
    abstract fun bindRelayManager(impl: RelayManagerImpl): RelayManager
}
