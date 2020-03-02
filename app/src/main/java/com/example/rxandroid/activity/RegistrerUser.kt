package com.example.rxandroid.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.rxandroid.R
import com.example.rxandroid.api.NetworkClient.getRetrofitClient
import com.example.rxandroid.api.ServerApi
import com.example.rxandroid.model.StatusResponse
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import kotlinx.android.synthetic.main.registration_form.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

class RegistrerUser : AppCompatActivity() {

    lateinit var filePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_form)

        profilepic.setOnClickListener {
            ImagePicker.with(this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    1080,
                    1080
                )    //Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }

        register_button.setOnClickListener {
            uploadToServer(filePath)

//            val intent = Intent(this, Login::class.java)
//            startActivity(intent)
        }

        backLayout.setOnClickListener {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data
                //foodImage.setImageURI(fileUri)

                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, fileUri)
                    profilepic.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                //You can also get File Path from intent
                filePath = ImagePicker.getFilePath(data).toString()
                Log.i("MainActivity ", "FilePath: $filePath")

                //You can get File object from intent
                val file: File? = ImagePicker.getFile(data)
                Log.i("MainActivity", "Filename " + file?.name)
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadToServer(filePath: String) {
        val gson = Gson()

        val retrofit =
            getRetrofitClient()
        val uploadAPIs = retrofit!!.create(ServerApi::class.java)
        //Create a file object using file path
        val file = File(filePath)

        // Create a request body with file and image media type
        val fileReqBody: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)

        // Create MultipartBody.Part using file request-body,file name and part name
        val part =
            MultipartBody.Part.createFormData("image", file.name, fileReqBody)

        //Create request body with text description and text media type
        val description = RequestBody.create(MediaType.parse("text/plain"), "image-type")
        val fName = RequestBody.create(MediaType.parse("text/plain"), firstnameTxt.text.toString())
        val lName = RequestBody.create(MediaType.parse("text/plain"), lastnameTxt.text.toString())
        val email = RequestBody.create(MediaType.parse("text/plain"), emailTxt.text.toString())
        val password =
            RequestBody.create(MediaType.parse("text/plain"), passwordTxt.text.toString())

        //
        val call = uploadAPIs.uploadImage(part, description, fName, lName, email, password)

        call.enqueue(object : Callback<StatusResponse> {
            override fun onResponse(
                call: Call<StatusResponse>,
                response: Response<StatusResponse>
            ) {
                Log.i("ResponseString", gson.toJson(response.body()))
                if (response.isSuccessful) {
//                    if (response.body()?.status!!){
//
//                    }

                    response.body()?.status
                } else {

                }
            }

            override fun onFailure(call: Call<StatusResponse>, t: Throwable?) {
                Log.i("ResponseFailure1", t?.message)
            }
        })
    }

    fun Activity.isHasPermission(vararg permissions: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            permissions.all { singlePermission ->
                applicationContext.checkSelfPermission(singlePermission) == PackageManager.PERMISSION_GRANTED
            }
        else true
    }

    fun Activity.askPermission(vararg permissions: String, requestCode: Int) =
        ActivityCompat.requestPermissions(this, permissions, requestCode)
}