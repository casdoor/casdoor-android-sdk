package com.casbin.casdoor.Models

internal data class CasdoorResponse<D1, D2> where D1 : Decodable, D2 : Decodable(
internal val status: String,
internal val msg: String,
internal val data: D1?,
internal val data2: D2?): Decodable
{
  internal fun isOk() {
    if (status == "error") {
      throw CasdoorError.init(error = . responseMessage (msg))
    }
  }
}
internal typealias CasdoorOneDataResponse<D : Decodable> = CasdoorResponse<D, String>
internal typealias CasdoorNoDataResponse = CasdoorResponse<String, String>


