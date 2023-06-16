package com.example.checkin

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import org.json.JSONObject

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

object CheckInService {
    private val BASE_URL = "http://10.0.2.2:3000"
    val API = checkInAPI()

    private fun initialiseRetrofit(): Retrofit{
        return Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(MoshiConverterFactory.create()).build()
    }

    private fun checkInAPI(): CheckInAPIService{
       return  initialiseRetrofit().create(CheckInAPIService::class.java)
    }



}

interface CheckInAPIService {
    @POST("/api/wss/userLogin")

    suspend fun login(@Body request: UserLoginRequest): Response<ResponseData>

}

@JsonClass(generateAdapter = true)
data class ResponseData(
    var status: String,
    var result: Map<String, Any>
)
@JsonClass(generateAdapter = true)
data class FailureBody(
    var message: String
)

@JsonClass(generateAdapter = true)
data class UserLoginRequest(
    @field:Json(name = "email")var email: String,
    @field:Json(name = "password")var password: String,
    @field:Json(name = "accesskey")var accesskey: String
)

@JsonClass(generateAdapter = true)
data class SuccessBody(
    var accountid: String,
    var email: String,
    var orgid: String
)