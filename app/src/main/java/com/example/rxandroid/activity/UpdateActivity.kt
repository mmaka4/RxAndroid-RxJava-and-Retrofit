package com.example.rxandroid.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rxandroid.R
import com.example.rxandroid.api.ServerApi
import com.example.rxandroid.model.MatundaResponse
import com.example.rxandroid.model.Tunda
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_fruits_layout.*
import kotlinx.android.synthetic.main.update_layout.*
import kotlinx.android.synthetic.main.update_layout.backLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


class UpdateActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.update_layout)

        Log.i("Update Activity: ", "Triggered")

        val tundaString = intent.getStringExtra("tundaData")
        val gson = Gson()
        val tunda = gson.fromJson<Tunda>(tundaString,
            Tunda::class.java)

        Log.i("Fetched Data", tunda.id+" "+tunda.name+" "+tunda.price)

        val resId = this.resources.getIdentifier(tunda.image, "id", packageName)
        Log.i("Resorce Image ", ""+resId)

        fruitName.setText(tunda.name)
        fruitPrice.setText(tunda.price)
        //foodImage.setImageResource(resId)
        Picasso.get().load(resources.getString(R.string.imageFruitsURL)+tunda.image).into(foodImage)

        foodImage.setOnClickListener {
            ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }

        update_button.setOnClickListener {
            val Id = tunda.id
            val fname: String = fruitName.text.toString()
            val fprice: String = fruitPrice.text.toString()

            if (Id != null)
                update(Id, fname, fprice)
        }

        backLayout.setOnClickListener {
            finish()
        }

    }

    private fun update(id:String, fruitName:String, fruitPrice:String){

        Log.i("EditText Values",id+" "+fruitName+" "+fruitPrice)
        val retrofit = Retrofit.Builder().baseUrl(getString(R.string.serverURL)).addConverterFactory(
            GsonConverterFactory.create()).build()

        val api = retrofit.create(ServerApi::class.java)

        val call = api.updateTunda(id, fruitName, fruitPrice)

        val gson = Gson()

        call.enqueue(object : Callback<MatundaResponse> {

            override fun onResponse(
                call: Call<MatundaResponse>,
                response: Response<MatundaResponse>
            ) {
                Log.i("ResponseString",gson.toJson(response.body()))

                if(response.isSuccessful){
                    if (response.body()?.status!!){
                        val intent = Intent(this@UpdateActivity, ListFruits::class.java)
                        startActivity(intent)
                    }

//                    response.body()?.matunda
                }else{

                }
            }

            override fun onFailure(call: Call<MatundaResponse>, t: Throwable) {
                Log.i("ResponseFailure1",t.message)
            }

        })

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
                    foodImage.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                //You can get File object from intent
                //val file: File? = ImagePicker.getFile(data)

                //You can also get File Path from intent
                val filePath: String? = ImagePicker.getFilePath(data)
                Log.i("FilePath: ", filePath)
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}