package example.spring.boot.advanced.e2e.gateways.common

import example.spring.boot.advanced.e2e.security.SecurityContextAuthorizationHeaderInterceptor
import okhttp3.OkHttpClient

fun defaultHttpClient() = OkHttpClient.Builder()
    .withAuthorizationHeaderInterceptor()
    .build()

fun OkHttpClient.Builder.withAuthorizationHeaderInterceptor() =
    addInterceptor(SecurityContextAuthorizationHeaderInterceptor())
