/*
 * Copyright 2021 The casbin Authors. All Rights Reserved.
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

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.HttpUrl

data class CodeRequestQuery(
    var clientID: String,
    var responseType: String,
    var scope: String,
    var state: String,
    var nonce: String,
    var codeChallengeMethod: String,
    var codeChallenge: String,
    var redirectUri: String
) {

    constructor(
        config: CasdoorConfig,
        nonce: String,
        scope: String?,
        state: String?,
        codeVerifier: String
    ) :
            this(
                config.clientID,
                "code",
                scope ?: "read",
                state ?: config.appName,
                nonce,
                "S256",
                Utils.generateCodeChallenge(codeVerifier),
                config.redirectUri
            )

    fun toHttpUrl(httpUrl: HttpUrl): HttpUrl {
        val builder = httpUrl.newBuilder()
        builder.addQueryParameter("client_id", clientID)
        builder.addQueryParameter("response_type", responseType)
        builder.addQueryParameter("scope", scope)
        builder.addQueryParameter("state", state)
        builder.addQueryParameter("nonce", nonce)
        builder.addQueryParameter("code_challenge_method", codeChallengeMethod)
        builder.addQueryParameter("code_challenge", codeChallenge)
        builder.addQueryParameter("redirect_uri", redirectUri)
        return builder.build()
    }
}

data class AccessTokenRequest(
    val grantType: String = "authorization_code",
    val clientID: String,
    val code: String,
    val verifier: String
) {

    fun toHttpUrl(httpUrl: HttpUrl): HttpUrl {
        return httpUrl.newBuilder()
            .addQueryParameter("grant_type", grantType)
            .addQueryParameter("client_id", clientID)
            .addQueryParameter("code", code)
            .addQueryParameter("code_verifier", verifier)
            .build()
    }

}

@JsonClass(generateAdapter = true)
data class RenewAccessTokenRequest(
    val grantType: String = "refresh_token",
    val clientID: String,
    val scope: String,
    val refreshToken: String
) {

    fun toHttpUrl(httpUrl: HttpUrl): HttpUrl {
        return httpUrl.newBuilder()
            .addQueryParameter("grant_type", grantType)
            .addQueryParameter("client_id", clientID)
            .addQueryParameter("refresh_token", refreshToken)
            .addQueryParameter("scope", scope)
            .build()
    }

}

@JsonClass(generateAdapter = true)
data class AccessTokenResponse(
    @Json(name = "access_token") var accessToken: String,
    @Json(name = "error") var error: String,
    @Json(name = "expires_in") var expiresIn: Int,
    @Json(name = "id_token") var idToken: String? = null,
    @Json(name = "refresh_token") var refreshToken: String? = null,
    @Json(name = "scope") var scope: String,
    @Json(name = "token_type") var tokenType: String
)

@JsonClass(generateAdapter = true)
data class CasdoorResponse<D1, D2>(
    @Json(name = "status") var status: String,
    @Json(name = "msg") var msg: String,
    @Json(name = "data") var data: D1?,
    @Json(name = "data2") var data2: D2?
) {
    fun isSuccessful(): Boolean {
        return status != "error"
    }
}

typealias CasdoorOneDataResponse<D> = CasdoorResponse<D, String>
typealias CasdoorNoDataResponse = CasdoorResponse<String, String>