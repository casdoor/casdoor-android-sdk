
package org.casbin.casdoor

import org.junit.Test
import org.junit.Assert.*

internal class UtilsTest {

    @Test
    fun generateCodeVerifier() {
        val codeVerifier = Utils.generateCodeVerifier()
        assertEquals(84, codeVerifier.length)
    }

    @Test
    fun generateRandomString() {
        val randomString = Utils.generateRandomString(10)
        println(randomString)
        assertEquals(10, randomString.length)
    }

}