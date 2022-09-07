package org.casbin.casdoor

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class CasdoorConfigTest {
    @Test
    fun testCasdoorConfig() {

        val casdoorConfig1 = CasdoorConfig(
            clientID = "",
            organizationName = "",
            redirectUri = "",
            endpoint = "bar/",
            appName = ""
        )

        assertEquals("bar/api/", casdoorConfig1.apiEndpoint)

        val casdoorConfig2 = CasdoorConfig(
            clientID = "",
            organizationName = "",
            redirectUri = "",
            endpoint = "",
            appName = "",
            apiEndpoint = "foo"
        )
        assertEquals("foo/", casdoorConfig2.apiEndpoint)

    }
}