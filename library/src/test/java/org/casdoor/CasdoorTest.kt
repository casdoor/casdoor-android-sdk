package org.casdoor

import org.junit.Assert.assertNotNull
import org.junit.Test
import java.io.IOException


internal class CasdoorTest {

    private val casdoorConfig = CasdoorConfig(
        endpoint = "https://door.casdoor.com",
        clientID = "014ae4bd048734ca2dea",
        organizationName = "casbin",
        redirectUri = "casdoor://callback",
        appName = "app-casnode"
    )

    private val casdoor = Casdoor(casdoorConfig)


    @Test
    fun getSignInUrl() {
        assertNotNull(casdoor.getSignInUrl())
    }

    @Test
    fun getSignUpUrl() {
        assertNotNull(casdoor.getSignUpUrl())
    }

    @Test
    fun requestOauthAccessToken() {
        println(casdoor.getSignInUrl())
        try {
            val accessToken = casdoor.requestOauthAccessToken("code")
            println(accessToken)
        } catch (e: IOException) {
            assertNotNull(e)
        }
    }

    @Test
    fun renewToken() {
    }

    @Test
    fun logout() {
    }
}