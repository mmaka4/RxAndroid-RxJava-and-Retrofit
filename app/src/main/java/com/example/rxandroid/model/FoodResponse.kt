package com.example.rxandroid.model

import com.google.gson.annotations.SerializedName

class Foody{
    @SerializedName("id")
    var id:String? = null
    @SerializedName("name")
    var name:String? = null
    @SerializedName("price")
    var price:String? = null
    @SerializedName("image")
    var image:String? = null
}

class FoodResponse {
    @SerializedName("foods")
    var food: ArrayList<Foody>? = null
}

