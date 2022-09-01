package com.casbin.casdoor


data class CasdoorConfig(
  var clientID: String,
  var organizationName: String,
  var redirectUri: String,
  var apiEndpoint: String,
  var endpoint: String,
  var appName: String
) {

  constructor(
    endpoint: String,
    clientID: String,
    organizationName: String,
    redirectUri: String,
    appName: String,
    apiEndpoint: String? = null
  ) : this() {
    this.clientID = clientID
    this.organizationName = organizationName
    this.redirectUri = redirectUri
    this.appName = appName
    this.endpoint = formatEndpoint(url = endpoint)
    val apiEndpoint = apiEndpoint
    if (apiEndpoint != null) {
      this.apiEndpoint = formatEndpoint(url = apiEndpoint)
    } else {
      this.apiEndpoint = this.endpoint + "api/"
    }
  }
}

private fun formatEndpoint(url: String): String {
  val url = url.trimmingCharacters(in = . whitespaces)
  return if (url.hasSuffix("/")) url else url + "/"
}
