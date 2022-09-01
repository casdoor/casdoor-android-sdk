package com.casbin.casdoor.Utils

import kotlin.String

class String {
  val String.parametersFromQueryString: Map<String, String>
    get() = dictionaryBySplitting("&", keyValueSeparator = "=")

  private fun String.dictionaryBySplitting(elementSeparator: String, keyValueSeparator: String) : Map<String, String> {
    var string = this
    if (hasPrefix(elementSeparator)) {
      string = String(dropFirst(1))
    }
    var parameters = mapOf<String, String>()
    val scanner = Scanner(string = string)
    while (!scanner.isAtEnd) {
      val key = scanner.scanUpToString(keyValueSeparator)
      scanner.scanString(keyValueSeparator)
      val value = scanner.scanUpToString(elementSeparator)
      scanner.scanString(elementSeparator)
      val key = key
      if (key != null) {
        val value = value
        if (value != null) {
          if (key.contains(elementSeparator)) {
            var keys = key.components(separatedBy = elementSeparator)
            val key = keys.popLast()
            if (key != null) {
              parameters.updateValue(value, forKey = String(key))
            }
            for (flag in keys) {
              parameters.updateValue("", forKey = flag)
            }
          } else {
            parameters.updateValue(value, forKey = key)
          }
        } else {
          parameters.updateValue("", forKey = key)
        }
      }
    }
    return parameters
  }


}