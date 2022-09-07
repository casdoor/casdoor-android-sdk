package org.casbin.casdoor

//public let clientID: String
//public let organizationName: String
//public let redirectUri: String
//public let apiEndpoint: String
//public let endpoint: String
//public let appName: String

data class CasdoorConfig(
    val clientID: String,
    val organizationName: String,
    val redirectUri: String,
    val endpoint: String,
    val appName: String,
    var apiEndpoint: String? = null
) {


    init {
        apiEndpoint = if (apiEndpoint != null) {
            val apiEndpointTemp = apiEndpoint!!.trim()
            if (apiEndpointTemp.endsWith("/")) apiEndpointTemp else "$apiEndpointTemp/"
        } else {
            "${endpoint}api/"
        }
    }


}
