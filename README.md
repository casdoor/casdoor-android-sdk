# casdoor-android-sdk

Casdoor's SDK for Android will allow you to easily connect your application to the Casdoor
authentication system without having to implement it from scratch.
Casdoor SDK is simple to use. We will show you the steps below.

## Step0. Adding the dependency

Add the following dependency to your app's build.gradle file:

```groovy
dependencies {
    implementation 'org.casdoor:casdoor-android-sdk:x.x.x' // not upload to maven yet...
}
```

## Step1. Init Config

Initialization requires 5 parameters, which are all str type:
| Name (in order)  | Must | Description |
| ---------------- | ---- | --------------------------------------------------- |
| endpoint | Yes | Casdoor Server Url, such as `https://door.casdoor.com` |
| clientID | Yes | Application.clientID |
| appName | Yes | Application name |
| organizationName | Yes |Organization name |

```kotlin
val casdoorConfig = CasdoorConfig(
   endpoint = "https://door.casdoor.com",
   clientID = "294b09fbc17f95daf2fe",
   redirectUri = "casdoor://callback",
   organizationName = "casbin",
   appName = "app-vue-python-example"
)
```

## Step2. Init Casdoor

The Casdoor Contains all APIs

```kotlin
val casdoor = Casdoor(casdoorConfig)
```

## Step3. Authorize with the Casdoor server

At this point, we should use some ways to verify with the Casdoor server.

To start, we want you understand clearly the verification process of Casdoor.
The following paragraphs will mention your app that wants to use Casdoor as a means
of verification as `APP`, and Casdoor as `Casdoor`.
`APP` will send a request to `Casdoor`. Since `Casdoor` is a UI-based OAuth
provider, you cannot use request management service like Postman to send a URL
with parameters and get back a JSON file.

casdoor-android-sdk support the url,you can use in webview or browser to verify.

```kotlin
casdoor.getSignInUrl()
```

Hints:

1. `redirect_uri` is the URL that your `APP` is configured to
   listen to the response from `Casdoor`. For example, if your `redirect_uri`
   is `casdoor://callback`, then Casdoor will send a request to this URL along with two
   parameters `code` and `state`, which will be used in later steps for authentication.
2. `state` is usually your Application's name, you can find it under the `Applications` tab
   in `Casdoor`, and the leftmost `Name` column gives each application's name.
3. The authorize URL allows the user to connect to a provider and give access to your application.
4. After Casdoor verification passed, it will be redirected to your `redirect_uri`,
   like `casdoor://callback?code=xxx&state=yyyy`.you can catch it and get the `code` and `state`,
   then call `requestOauthAccessToken()` and parse out jwt token.

# Example

See at: https://github.com/casdoor/casdoor-android-example
