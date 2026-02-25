package com.bih.applicationsmurfforyou.di

import com.bih.applicationsmurfforyou.data.util.ConnectivityObserver
import com.bih.applicationsmurfforyou.data.util.NetworkConnectivityObserver
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindConnectivityObserver(connectivityObserver: NetworkConnectivityObserver): ConnectivityObserver
}
