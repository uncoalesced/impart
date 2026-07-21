// Engineered by uncoalesced
package com.uncoalesced.impart.core.di

import com.uncoalesced.impart.data.repository.ContactRepositoryImpl
import com.uncoalesced.impart.domain.repository.ContactRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindContactRepository(impl: ContactRepositoryImpl): ContactRepository
}
