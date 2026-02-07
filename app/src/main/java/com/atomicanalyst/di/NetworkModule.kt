package com.atomicanalyst.di

import com.atomicanalyst.BuildConfig
import com.atomicanalyst.data.network.AuthHeaderInterceptor
import com.atomicanalyst.data.network.NetworkSecurityConfig
import com.atomicanalyst.data.network.PinConfigParser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.CertificatePinner
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideNetworkSecurityConfig(): NetworkSecurityConfig = NetworkSecurityConfig(
        baseUrl = BuildConfig.API_BASE_URL,
        pins = PinConfigParser.parse(BuildConfig.CERT_PINS)
    )

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authHeaderInterceptor: AuthHeaderInterceptor,
        config: NetworkSecurityConfig
    ): OkHttpClient {
        val connectionSpec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
            .tlsVersions(TlsVersion.TLS_1_3, TlsVersion.TLS_1_2)
            .build()
        val builder = OkHttpClient.Builder()
            .connectionSpecs(listOf(connectionSpec))
            .addInterceptor(authHeaderInterceptor)
        if (config.pins.isNotEmpty()) {
            val pinner = CertificatePinner.Builder().apply {
                config.pins.forEach { pin ->
                    add(pin.host, pin.sha256)
                }
            }.build()
            builder.certificatePinner(pinner)
        }
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        config: NetworkSecurityConfig
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(config.baseUrl)
            .client(okHttpClient)
            .build()
    }
}
