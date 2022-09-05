package ru.cherepanov.apps.dictionary.di.modules

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.create
import ru.cherepanov.apps.dictionary.BuildConfig
import ru.cherepanov.apps.dictionary.data.RemoteSourceImpl
import ru.cherepanov.apps.dictionary.data.network.DictionaryWebService
import ru.cherepanov.apps.dictionary.domain.repository.RemoteSource
import javax.inject.Singleton

@Module(includes = [NetworkModule.BindsModule::class])
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideDictionaryWebService(
        okHttpClient: OkHttpClient,
        callAdapterFactory: CallAdapter.Factory
    ): DictionaryWebService {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(DICT_SERVICE_URL)
            .addCallAdapterFactory(callAdapterFactory)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create()
    }

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(
            HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            }).build()
    }

    @Provides
    fun provideCallAdapterFactory(): CallAdapter.Factory {
        return RxJava2CallAdapterFactory.create()
    }

    @Module
    @InstallIn(SingletonComponent::class)
    interface BindsModule {
        @Binds
        fun bindRemoteSource(remoteSource: RemoteSourceImpl): RemoteSource
    }

    private companion object {
        const val DICT_SERVICE_URL = "http://192.168.43.31:8000/"
    }
}