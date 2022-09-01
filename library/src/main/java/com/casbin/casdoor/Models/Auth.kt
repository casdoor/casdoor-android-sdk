package com.casbin.casdoor.Models

import com.casbin.casdoor.CasdoorConfig
import com.casbin.casdoor.Utils.Utils
import com.google.firebase.encoders.annotations.Encodable

internal data class CodeRequestQuery(
  internal var clientID: String,
  internal var responseType: String,
  internal var scope: String,
  internal var state: String,
  internal var nonce: String,
  internal var codeChallengeMethod: String,
  internal var codeChallenge: String,
  internal var redirectUri: String
) : Encodable() {
  internal enum class CodingKeys(val rawValue: String) : CodingKey {
    clientID("client_id"), responseType("response_type"), scope("scope"), state("state"), nonce("nonce"), codeChallengeMethod(
      "code_challenge_method"
    ),
    codeChallenge("code_challenge"), redirectUri("redirect_uri");

    companion object {
      operator fun invoke(rawValue: String) =
        CodingKeys.values().firstOrNull { it.rawValue == rawValue }
    }
  }

  internal constructor(
    config: CasdoorConfig,
    nonce: String,
    scope: String? = null,
    state: String? = null,
    codeVerifier: String
  ) {
    this.clientID = config.clientID
    this.responseType = "code"
    this.redirectUri = config.redirectUri
    this.scope = scope ?: "read"
    this.state = state ?: config.appName
    this.codeChallengeMethod = "S256"
    this.nonce = nonce
    this.codeChallenge = Utils.Utils.generateCodeChallenge(codeVerifier)
  }

  internal fun toUrl(request: URLRequest): URLRequest =
    URLEncodedFormParameterEncoder.default.encode(this, into = request)
}

internal data class AccessTokenRequest(
  internal var grantType: String,
  internal var clientID: String,
  internal var code: String,
  internal var verifier: String
) : Encodable() {

  internal constructor(
    grantType: String = "authorization_code",
    clientID: String,
    code: String,
    verifier: String
  ) {
    this.grantType = grantType
    this.clientID = clientID
    this.code = code
    this.verifier = verifier
  }

  internal enum class CodingKeys(val rawValue: String) : CodingKey {
    clientID("client_id"), code("code"), grantType("grant_type"), verifier("code_verifier");

    companion object {
      operator fun invoke(rawValue: String) =
        CodingKeys.values().firstOrNull { it.rawValue == rawValue }
    }
  }
}

internal data class ReNewAccessTokenRequest(
  internal val grantType: String,
  internal val clientID: String,
  internal val scope: String,
  internal val refreshToken: String
) : Encodable {

  internal constructor(
    grantType: String = "refresh_token",
    clientID: String,
    scope: String,
    refreshToken: String
  ) {
    this.grantType = grantType
    this.clientID = clientID
    this.scope = scope
    this.refreshToken = refreshToken
  }

  internal enum class CodingKeys(val rawValue: String) : CodingKey {
    clientID("client_id"), scope("scope"), grantType("grant_type"), refreshToken("refresh_token");

    companion object {
      operator fun invoke(rawValue: String) =
        CodingKeys.values().firstOrNull { it.rawValue == rawValue }
    }
  }
}

data class AccessTokenResponse(
  val accessToken: String,
  val idToken: String?,
  val refreshToken: String?,
  val tokenType: String,
  val expiresIn: Int,
  val scope: String
) : Decodable {
  internal enum class CodingKeys(val rawValue: String) : CodingKey {
    accessToken("access_token"), idToken("id_token"), tokenType("token_type"), refreshToken("refresh_token"), expiresIn(
      "expires_in"
    ),
    scope("scope");

    companion object {
      operator fun invoke(rawValue: String) =
        CodingKeys.values().firstOrNull { it.rawValue == rawValue }
    }
  }
}
