# casdoor-android-sdk

[![Build](https://github.com/casdoor/casdoor-android-sdk/actions/workflows/build.yaml/badge.svg)](https://github.com/casdoor/casdoor-android-sdk/actions/workflows/build.yaml)
[![Maven Central](https://img.shields.io/maven-central/v/org.casbin/casdoor-android-sdk.svg)](https://central.sonatype.com/artifact/org.casbin/casdoor-android-sdk)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?logo=android)](https://developer.android.com/about/versions/lollipop)
[![Kotlin](https://img.shields.io/badge/kotlin-2.2-7F52FF.svg?logo=kotlin)](https://kotlinlang.org/)
[![License](https://img.shields.io/github/license/casdoor/casdoor-android-sdk.svg)](LICENSE)
[![Discord](https://img.shields.io/discord/1022748306096537660?logo=discord&label=discord&color=5865F2)](https://discord.gg/5rPsrAzK7S)

The Android SDK of [Casdoor](https://casdoor.ai/). It signs users of an Android app in with Casdoor through the OAuth 2.0
authorization code flow with [PKCE](https://datatracker.ietf.org/doc/html/rfc7636), so the app does not need to (and
should not) contain a client secret.

The SDK is written in Kotlin and can be called from Java as well. A complete demo app is at
[casdoor-android-example](https://github.com/casdoor/casdoor-android-example).

## Installation

The SDK is published to [Maven Central](https://central.sonatype.com/artifact/org.casbin/casdoor-android-sdk). It needs
`minSdk` 21 or higher.

```groovy
dependencies {
    implementation 'org.casbin:casdoor-android-sdk:0.1.0'
}
```

Your app also needs the internet permission in `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## Configure Casdoor

In the Casdoor web UI, open your application and add the redirect URI of your app, such as `casdoor://callback`, to
**Redirect URLs**. Casdoor redirects to this URI with the authorization code after the user signs in.

## Usage

### 1. Create the client

```kotlin
val casdoor = Casdoor(
    CasdoorConfig(
        endpoint = "https://door.casdoor.com",
        clientID = "014ae4bd048734ca2dea",
        organizationName = "casbin",
        redirectUri = "casdoor://callback",
        appName = "app-casnode"
    )
)
```

| Field            | Description                                             |
|------------------|---------------------------------------------------------|
| endpoint         | URL of the Casdoor server, such as `https://door.casdoor.com` |
| clientID         | Client ID of the Casdoor application                    |
| organizationName | Name of the Casdoor organization                        |
| redirectUri      | Redirect URI of your app, added to the application in Casdoor |
| appName          | Name of the Casdoor application                         |

### 2. Open the sign-in page

`getSignInUrl()` returns the Casdoor sign-in page URL (use `getSignUpUrl()` for the sign-up page). Load it in a WebView
and catch the redirect to get the authorization code:

```kotlin
webView.webViewClient = object : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        val uri = request.url
        if (uri.scheme == "casdoor") {
            val code = uri.getQueryParameter("code")
            // exchange the code for tokens, see the next step
            return true
        }
        return false
    }
}
webView.settings.javaScriptEnabled = true
webView.loadUrl(casdoor.getSignInUrl(scope = "profile"))
```

You can also open the URL in a browser or a Custom Tab and receive the redirect with an `<intent-filter>` for the
`casdoor` scheme.

### 3. Exchange the code for tokens

```kotlin
val token = casdoor.requestOauthAccessToken(code)
val accessToken = token.accessToken
val refreshToken = token.refreshToken
```

`getSignInUrl()` generates a PKCE code verifier, and `requestOauthAccessToken()` sends it to Casdoor. If the activity
may be recreated while the user is signing in, save `casdoor.codeVerifier` (for example in `onSaveInstanceState`) and
pass it back with `requestOauthAccessToken(code, savedCodeVerifier)`.

### 4. Get the user, refresh the token and sign out

```kotlin
val userInfo = casdoor.getUserInfo(accessToken)   // name, email, avatar, ...
val newToken = casdoor.renewToken(refreshToken)
casdoor.logout(accessToken)
```

All methods except `getSignInUrl()` and `getSignUpUrl()` make blocking network calls. Call them from a background thread
or a coroutine, not the main thread. Errors are thrown as `IOException`, with the OAuth error from Casdoor (such as
`invalid_grant: ...`) in the message.

## Use from Java

```java
CasdoorConfig config = new CasdoorConfig(
        "014ae4bd048734ca2dea",     // clientID
        "casbin",                   // organizationName
        "casdoor://callback",       // redirectUri
        "https://door.casdoor.com", // endpoint
        "app-casnode"               // appName
);
Casdoor casdoor = new Casdoor(config);

String signInUrl = casdoor.getSignInUrl();
String codeVerifier = casdoor.getCodeVerifier();

// after the redirect, on a background thread:
AccessTokenResponse token = casdoor.requestOauthAccessToken(code, codeVerifier);
UserInfo userInfo = casdoor.getUserInfo(token.getAccessToken());
```

## API

| Method                                          | Description                                       |
|-------------------------------------------------|---------------------------------------------------|
| `getSignInUrl(scope?, state?)`                  | URL of the sign-in page, starts a new PKCE flow    |
| `getSignUpUrl(scope?, state?)`                  | URL of the sign-up page                           |
| `requestOauthAccessToken(code, codeVerifier?)`  | Exchanges the authorization code for tokens       |
| `renewToken(refreshToken, scope?)`              | Gets new tokens with the refresh token            |
| `getUserInfo(accessToken)`                      | Gets the signed-in user (OIDC userinfo)           |
| `logout(accessToken, state?)`                   | Signs the user out of Casdoor                     |

`scope` defaults to `read` and `state` defaults to `appName`.

## License

[Apache-2.0](LICENSE)
