package com.example.rxandroid.model

import com.google.gson.annotations.SerializedName


class User {
    @SerializedName("id")
    var id: String? = null
    @SerializedName("firstName")
    var firstName: String? = null
    @SerializedName("lastName")
    var lastName: String? = null
    @SerializedName("email")
    var email: String? = null
    @SerializedName("password")
    var password: String? = null
    @SerializedName("image")
    var image: String? = null
}

class UserResponse {
    @SerializedName("status")
    var status: Boolean? = null

    @SerializedName("user")
    var user: User? = null

}