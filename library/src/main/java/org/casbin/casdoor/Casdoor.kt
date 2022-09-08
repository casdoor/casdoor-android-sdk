/*
 * Copyright 2022 The casbin Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.casbin.casdoor

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.EMPTY_REQUEST
import okio.IOException
import java.lang.reflect.Type


class Casdoor(private val config: CasdoorConfig) {

    private var codeVerifier: String? = null
    private var nonce: String? = null

    private val client = OkHttpClient()
    private val moshi = Moshi.Builder().build()
    private val accessTokenResponseAdaptor = moshi.adapter(AccessTokenResponse::class.java)
    private val casdoorResponseType: Type = Types.newParameterizedType(
        CasdoorResponse::class.java,
        String::class.java,
        String::class.java
    )
    private val casdoorNoDataResponseAdaptor =
        moshi.adapter<CasdoorResponse<String, String>>(casdoorResponseType)

    /**
     * Get the sign in URL.
     */
    fun getSignInUrl(scope: String? = null, state: String? = null): String {
        this.codeVerifier = Utils.generateCodeVerifier()
        val url = "${config.endpoint}login/oauth/authorize"
        this.nonce = Utils.generateNonce()
        val query = CodeRequestQuery(
            config = config,
            nonce = nonce!!,
            scope = scope,
            state = state,
            codeVerifier = codeVerifier!!
        )

        val httpUrl = url.toHttpUrlOrNull()

        httpUrl ?: throw IllegalArgumentException("Invalid URL")

        return query.toHttpUrl(httpUrl).toString()
    }

    /**
     * Get the sign up URL.
     */
    fun getSignUpUrl(scope: String? = null, state: String? = null): String {
        val urlString =
            getSignInUrl(scope, state).replace("/login/oauth/authorize", "/signup/oauth/authorize")
        return urlString.toHttpUrlOrNull()?.toString()
            ?: throw IllegalArgumentException("Invalid URL")
    }

    /**
     * Get Access Token.
     */
    fun requestOauthAccessToken(code: String): AccessTokenResponse {

        var httpUrl = "${config.apiEndpoint}login/oauth/access_token".toHttpUrlOrNull()
        httpUrl ?: throw IllegalArgumentException("Invalid URL")
        httpUrl = AccessTokenRequest(
            code = code,
            verifier = codeVerifier!!,
            clientID = config.clientID
        ).toHttpUrl(httpUrl)

        client.newCall(Request.Builder().url(httpUrl).post(EMPTY_REQUEST).build()).execute().use {
            if (!it.isSuccessful) throw IOException("Unexpected code $it")
            val accessToken: AccessTokenResponse? =
                accessTokenResponseAdaptor.fromJson(it.body!!.source())
            accessToken?.refreshToken ?: throw IOException("response error: $it")
            return accessToken
        }

    }

    /**
     * Renew Access Token.
     */
    fun renewToken(refreshToken: String, scope: String? = null): AccessTokenResponse {

        var httpUrl = "${config.apiEndpoint}login/oauth/refresh_token".toHttpUrlOrNull()
        httpUrl ?: throw IllegalArgumentException("Invalid URL")
        httpUrl = RenewAccessTokenRequest(
            refreshToken = refreshToken,
            scope = scope ?: "read",
            clientID = config.clientID
        ).toHttpUrl(httpUrl)

        client.newCall(Request.Builder().url(httpUrl).post(EMPTY_REQUEST).build()).execute().use {
            if (!it.isSuccessful) throw IOException("Unexpected code $it")
            val accessToken: AccessTokenResponse? =
                accessTokenResponseAdaptor.fromJson(it.body!!.source())
            accessToken?.refreshToken ?: throw IOException("response error: $it")
            return accessToken
        }

    }

    /**
     * Logout.
     */
    fun logout(idToken: String, state: String? = null): Boolean {
        var httpUrl = "${config.apiEndpoint}login/oauth/logout".toHttpUrlOrNull()
        httpUrl ?: throw IllegalArgumentException("Invalid URL")
        httpUrl = httpUrl.newBuilder()
            .addQueryParameter("id_token_hint", idToken)
            .addQueryParameter("state", state ?: config.appName)
            .build()

        client.newCall(Request.Builder().url(httpUrl).post(EMPTY_REQUEST).build()).execute().use {
            if (!it.isSuccessful) throw IOException("Unexpected code $it")
            val casdoorResponse: CasdoorNoDataResponse? =
                casdoorNoDataResponseAdaptor.fromJson(it.body!!.source())

            if (casdoorResponse?.isSuccessful() == false) {
                throw IOException("response error: $it")
            }

            val isAffected = casdoorResponse?.data
            return (isAffected != null && isAffected == "Affected")
        }

    }


}