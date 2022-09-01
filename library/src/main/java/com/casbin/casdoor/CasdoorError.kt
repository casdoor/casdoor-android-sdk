package com.casbin.casdoor

data class CasdoorError(internal val error: Error) : Swift.Error, CustomStringConvertible {
  companion object {
    /// URL provided to client is invalid
    val invalidURL: CasdoorError
      get() = .init(error = .invalidURL)
  }

  val description: String
    get() {
      when (error) {
        invalidURL -> return """
            The request url is invalid format.
            This error is internal. So please make a issue on https://github.com/casdoor/casdoor-ios-sdk/issues to solve it.
            """
        is responseMessage -> return "response error: ${s}"
        is invalidJwt -> return "invalidJWT: ${s}"
      }
    }

  internal sealed class Error {
    object invalidURL : Error()
    data class responseMessage(val v1: String) : Error()
    data class invalidJwt(val v1: String) : Error()
  }
}
