package com.casbin.casdoor.Utils

import android.provider.ContactsContract
import java.security.spec.MGF1ParameterSpec.SHA256

internal data class Utils {
  companion object {

    internal fun generate(count: Int): kotlin.String =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~".random(count = count)

    internal fun generateNonce(): kotlin.String =
      generate(count = 10)

    internal fun generateCodeVerifier(): kotlin.String =
      generate(count = 84)

    internal fun generateCodeChallenge(verifier: kotlin.String): kotlin.String =
      base64Url(base64 = ContactsContract.Contacts.Data(SHA256.hash(data = verifier.data(using = . ascii)!!)).base64EncodedString())

    internal fun base64Url(base64: kotlin.String): kotlin.String {
      var base64 =
        base64.replacingOccurrences(of = "+", with = "-").replacingOccurrences(of = "/", with = "_")
      if (base64.hasSuffix("=")) {
        base64.popLast()
      }
      return base64
    }
  }
}

internal fun kotlin.String.random(count: Int): kotlin.String {
  var array: List<Character> = .init(repeating = "0", count = count)(0 until count).forEach {
    val it
    array[it] = this.randomElement()!!
  }
  return String.init(array)
}
