package com.casbin.casdoor

import com.casbin.casdoor.Models.AccessTokenRequest
import com.casbin.casdoor.Models.AccessTokenResponse
import com.casbin.casdoor.Models.CodeRequestQuery
import com.casbin.casdoor.Models.ReNewAccessTokenRequest
import com.casbin.casdoor.Utils.Utils
import java.net.URL

final class Casdoor {

  constructor(config: CasdoorConfig) {
    this.config = config
  }

  val config: CasdoorConfig
  lateinit internal var codeVerifier: String
  lateinit internal var nonce: String
}

//Apis
internal fun Casdoor.getSigninUrl(scope: String? = null, state: String? = null): URL {
  this.codeVerifier = Utils.generateCodeVerifier()
  val url = "${config.endpoint}login/oauth/authorize"
  this.nonce = Utils.generateNonce()
  val query = CodeRequestQuery.init(
    config = config,
    nonce = nonce!!,
    scope = scope,
    state = state,
    codeVerifier = codeVerifier!!
  )
  val urlRequst: URLRequest = .init(url = url, method = .get)
  val uri = query.toUrl(request = urlRequst).url ?: throw CasdoorError.invalidURL
  return uri
}

class CasdoorError {

}

class URLRequest {

}

internal fun Casdoor.getSignupUrl(scope: String? = null, state: String? = null): URL {
  val urlString = getSigninUrl(
    scope = scope,
    state = state
  ).absoluteString.replacingOccurrences(
    of = "/login/oauth/authorize",
    with = "/signup/oauth/authorize"
  )
  val uri = URL.init(string = urlString) ?: throw CasdoorError.invalidURL
  return uri
}

internal fun Casdoor.requestOauthAccessToken(code:String): AccessTokenResponse {
  val query =
    AccessTokenRequest.init(clientID = config.clientID, code = code, verifier = codeVerifier)
  val url = "${config.apiEndpoint}login/oauth/access_token"
  val token = await
  AF.request(
    url,
    method = . post, parameters = query, encoder = URLEncodedFormParameterEncoder.default).serializingDecodable(AccessTokenResponse.self).value
  if (token.refreshToken == null) {
    throw CasdoorError.init(error = . responseMessage (token.accessToken))
  }
  return token
}

internal fun Casdoor.renewToken(refreshToken: String,scope: String? = null): AccessTokenResponse {
  val query = ReNewAccessTokenRequest.init(
    clientID = config.clientID,
    scope = scope ?: "read",
    refreshToken = refreshToken
  )
  val url = "${config.apiEndpoint}login/oauth/refresh_token"
  val token = await
  AF.request(
    url,
    method = . post, parameters = query, encoder = URLEncodedFormParameterEncoder.default).serializingDecodable(AccessTokenResponse.self).value
  if (token.refreshToken == null || token.refreshToken!!.isEmpty()) {
    throw CasdoorError.init(error = . responseMessage (token.accessToken))
  }
  return token
}

internal fun Casdoor.logout(idToken: String,state: String? = null): Boolean {
  val query = mapOf("id_token_hint" to idToken, "state" to state ?: config.appName)
  val url = "${config.apiEndpoint}login/oauth/logout"
  val resData = await
  AF.request(
    url,
    method = . post, parameters = query, encoder = URLEncodedFormParameterEncoder.default).serializingDecodable(CasdoorNoDataResponse.self).value
  resData.isOk()
  val isAffected = resData.data
  if (isAffected != null && !isAffected.isEmpty()) {
    return isAffected == "Affected"
  }
  return false
}
