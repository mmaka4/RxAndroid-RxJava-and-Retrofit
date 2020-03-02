package com.example.rxandroid.model

import com.google.gson.annotations.SerializedName

class Datum {

    @SerializedName("id")
    var id: String? = null
    @SerializedName("employee_name")
    var employeeName: String? = null
    @SerializedName("employee_salary")
    var employeeSalary: String? = null
    @SerializedName("employee_age")
    var employeeAge: String? = null
    @SerializedName("profile_image")
    var profileImage: String? = null
}

class DatumResponse {
    @SerializedName("status")
    var status:String? = null
    @SerializedName("data")
    var data: List<Datum>? = null
}