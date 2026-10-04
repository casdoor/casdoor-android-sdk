/*
 * Copyright 2026 The Casdoor Authors. All Rights Reserved.
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

package org.casdoor;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Makes sure the SDK can be called from Java.
 */
public class CasdoorJavaTest {
    private final CasdoorConfig casdoorConfig = new CasdoorConfig(
            "014ae4bd048734ca2dea",
            "casbin",
            "casdoor://callback",
            "https://door.casdoor.com",
            "app-casnode"
    );

    @Test
    public void getSignInUrl() {
        Casdoor casdoor = new Casdoor(casdoorConfig);
        assertTrue(casdoor.getSignInUrl().startsWith("https://door.casdoor.com/login/oauth/authorize"));
        assertTrue(casdoor.getSignInUrl("openid profile").contains("scope=openid%20profile"));
        assertNotNull(casdoor.getCodeVerifier());
        assertTrue(casdoor.getSignUpUrl().startsWith("https://door.casdoor.com/signup/oauth/authorize"));
    }
}
