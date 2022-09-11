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

import org.junit.Test

import org.junit.Assert.*

/**
 * Tests of CasdoorConfig.
 */
class CasdoorConfigTest {
    @Test
    fun testCasdoorConfig() {

        val casdoorConfig1 = CasdoorConfig(
            clientID = "",
            organizationName = "",
            redirectUri = "",
            endpoint = "http://bar.com/",
            appName = ""
        )

        assertEquals("http://bar.com/api/", casdoorConfig1.apiEndpoint)

        val casdoorConfig2 = CasdoorConfig(
            clientID = "",
            organizationName = "",
            redirectUri = "",
            endpoint = "",
            appName = "",
            apiEndpoint = "http://foo.com"
        )
        assertEquals("http://foo.com/", casdoorConfig2.apiEndpoint)

    }
}