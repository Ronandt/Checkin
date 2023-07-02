package com.example.checkin

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.json.JSONObject

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

object CheckInService {
    private val BASE_URL = "http://10.0.2.2:3000"
    val API = checkInAPI()

    private fun initialiseRetrofit(): Retrofit{
        return Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(MoshiConverterFactory.create()).client(
            OkHttpClient().newBuilder().connectTimeout(250, TimeUnit.MILLISECONDS).build()
        ).build()
    }

    private fun checkInAPI(): CheckInAPIService{
       return  initialiseRetrofit().create(CheckInAPIService::class.java)
    }



}

interface CheckInAPIService {
    @POST("/api/wss/userLogin")

    suspend fun login(@Body request: UserLoginRequest): Response<ResponseData>

    @POST("/api/wss/changePassword")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ResponseData>

    @POST("/api/wss/getProfileDetails")
    suspend fun getProfileDetails(@Body request: GetUserInfoRequest): Response<ResponseData>

    @POST("/api/wss/updateProfileDetails")
    suspend fun updateProfileDetails(@Body request: ChangeUserInfoRequest): Response<ResponseData>

    @GET("/api/getCheckedInDetails")
    suspend fun getCheckedInDetails(@Query("accesskey") accessKey: String): Response<ResponseBody>

    @POST("/api/checkin")
    suspend fun checkIn(@Body request: CheckInRequest): Response<ResponseData>

    @POST("/api/checkout")
    suspend fun checkOut(@Body request: CheckInRequest): Response<ResponseData>

    @GET("/api/getRecords/{accessKey}")
    suspend fun getRecords(@Path(value = "accessKey") accessKey: String): Response<ResponseBody>

    @POST("/api/editEntry")
    suspend fun editEntry(@Body request: UpdateRequest): Response<ResponseData>
    @POST("/api/deleteEntry")
    suspend fun deleteEntry(@Body request: DeleteRequest): Response<ResponseData>
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
data class CheckInRequest(@field:Json(name = "room_id") val roomId:String, @field:Json(name = "accesskey") val accessKey: String)
@JsonClass(generateAdapter = true)
data class UserLoginRequest(
    @field:Json(name = "email")var email: String,
    @field:Json(name = "password")var password: String,
    @field:Json(name = "accesskey")var accesskey: String
)

@JsonClass(generateAdapter = true)
data class ChangePasswordRequest(
    @field:Json(name = "accountid") var accountId: String,
    @field:Json(name = "oldpwd") var oldPassword: String,
    @field:Json(name = "newpwd") var newPassword: String,
    @field:Json(name = "cnfmpwd") var confirmPassword: String,
    @field:Json(name = "accesskey") var accessKey: String
)

@JsonClass(generateAdapter = true)
data class DeleteRequest(
    @field:Json(name = "entry_id") var entryId: String,
    @field:Json(name = "accesskey") var accessKey: String
)

@JsonClass(generateAdapter = true)
data class UpdateRequest(
    @field:Json(name = "date") var date: String,
    @field:Json(name = "accesskey") var accessKey: String,
    @field:Json(name = "entry_id") var entryId: String,
    @field:Json(name = "time_in") var timeIn: Long,
    @field:Json(name = "time_out") var timeOut: Long

)
@JsonClass(generateAdapter = true)
data class ChangeUserInfoRequest(
    @field:Json(name = "accountid") var accountId: String,
    @field:Json(name = "accessKey") var accessKey: String,
    @field:Json(name = "username") var username: String,
    @field:Json(name = "email") var email: String,
    @field:Json(name = "organisation") var organisation: String
)

@JsonClass(generateAdapter = true)
data class GetUserInfoRequest(
    @field:Json(name = "accountid") var accountId: String,
)

@JsonClass(generateAdapter = true)
data class SuccessBody(
    var accountid: String,
    var email: String,
    var orgid: String
)