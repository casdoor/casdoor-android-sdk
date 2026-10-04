package org.casdoor

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
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
    fun requestOauthAccessTokenWithSavedVerifier() {
        // a new instance, e.g. after the activity is recreated, uses the verifier saved before
        casdoor.getSignInUrl()
        val codeVerifier = casdoor.codeVerifier!!
        try {
            Casdoor(casdoorConfig).requestOauthAccessToken("code", codeVerifier)
            fail("an invalid code should be rejected")
        } catch (e: IOException) {
            assertTrue(e.message!!.startsWith("invalid_grant"))
        }
    }

    @Test(expected = IllegalStateException::class)
    fun requestOauthAccessTokenWithoutVerifier() {
        Casdoor(casdoorConfig).requestOauthAccessToken("code")
    }

    @Test
    fun renewToken() {
    }

    @Test
    fun logout() {
    }
}