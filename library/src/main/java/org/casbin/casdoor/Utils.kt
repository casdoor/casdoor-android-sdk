/*
 * Copyright 2022 The casbin Authors. All Rights Reserved.
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

import android.util.Base64
import java.security.MessageDigest
import java.util.*

internal object Utils {

    fun generateCodeVerifier(): String {
        return generateRandomString(84)
    }

    fun generateNonce(): String {
        return generateRandomString(10)
    }

    /**
     * generate random string with alphabet and number
     */
    fun generateRandomString(length: Int): String {

        val random = Random()
        val sb = StringBuilder()
        for (i in 0 until length) {
            val number = random.nextInt(3)
            var result = 0
            when (number) {
                0 -> result = random.nextInt(10) + 48 // 0-9
                1 -> result = random.nextInt(26) + 65 // A-Z
                2 -> result = random.nextInt(26) + 97 // a-z
            }
            sb.append(result.toChar())
        }
        return sb.toString()

    }

    fun generateCodeChallenge(verifier: String): String {
        // generate SHA256 hash
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(verifier.toByteArray())
        // encode hash with base64
        val base64String = Base64.encodeToString(hash, Base64.NO_WRAP or Base64.URL_SAFE or Base64.NO_PADDING)
        return base64Url(base64String)
    }

    private fun base64Url(base64: String): String {
        var s = base64.replace("+", "-").replace("/", "_")

        if (s.endsWith("=")) {
            s = s.substring(0, s.length - 1)
        }

        return s
    }

}