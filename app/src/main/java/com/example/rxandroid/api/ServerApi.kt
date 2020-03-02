package com.example.rxandroid.api

import com.example.rxandroid.model.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ServerApi {

    @GET("api/v1/employees")
    fun getEmployees(): retrofit2.Call<DatumResponse>

    @GET("matunda/details")
//    fun getMatunda(): retrofit2.Call<MatundaResponse>
    fun getMatunda(): Observable<MatundaResponse>

    @GET("food/details")
    fun getFood(): retrofit2.Call<FoodResponse>

    @FormUrlEncoded
    @POST("login/user")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): retrofit2.Call<UserResponse>

    @FormUrlEncoded
    @POST("update/tunda")
    fun updateTunda(
        @Field("id") id: String,
        @Field("fruitName") fruitName: String,
        @Field("fruitPrice") fruitPrice: String
    ): retrofit2.Call<MatundaResponse>

    @FormUrlEncoded
    @POST("delete/tunda")
    fun deleteTunda(
        @Field("id") id: String
    ): retrofit2.Call<MatundaResponse>

    @Multipart
    @POST("upload/image")
    fun uploadImage(@Part file: MultipartBody.Part,
                    @Part("description") imgDescription: RequestBody,
                    @Part("firstName") fName: RequestBody,
                    @Part("lastName") lName: RequestBody,
                    @Part("email") email: RequestBody,
                    @Part("password") password: RequestBody
                    ): retrofit2.Call<StatusResponse>
}