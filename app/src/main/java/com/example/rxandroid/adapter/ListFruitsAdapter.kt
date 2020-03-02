package com.example.rxandroid.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rxandroid.activity.UpdateActivity
import com.example.rxandroid.model.MatundaResponse
import com.example.rxandroid.R
import com.example.rxandroid.api.ServerApi
import com.example.rxandroid.model.Tunda
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_fruits_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import cn.pedant.SweetAlert.SweetAlertDialog

class ListFruitsAdapter(
    private val foodList: ArrayList<Tunda>,
    val context: Context,
    val activity: Activity
) : RecyclerView.Adapter<ListFruitsAdapter.FoodViewHolder>() {

    lateinit var lfAdapter: ListFruitsAdapter
    lateinit var mData: ArrayList<Tunda>
    var pos: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val view: View = layoutInflater.inflate(R.layout.list_fruits_item, parent, false)
        return FoodViewHolder(view, viewType)
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.name.text = foodList[position].name
        holder.price.text = foodList[position].price
        Picasso.get()
            .load(context.resources.getString(R.string.imageFruitsURL) + foodList[position].image)
            .into(holder.image)

        holder.editIconLayout.setOnClickListener {
            val gson = Gson()
            val intent = Intent(
                context,
                UpdateActivity::class.java
            )
            intent.putExtra("tundaData", gson.toJson(foodList[position]))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            Log.i("EditLayoutClicked: ", "Triggered")
        }

        holder.deleteIconLayout.setOnClickListener {
            val iD: String = foodList[position].id.toString()

            SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("You want to delete " + foodList[position].name + " data !")
                .setConfirmText("Delete!")
                .setConfirmClickListener { sweetAlertDialog: SweetAlertDialog? ->
                    sweetAlertDialog?.setTitleText("Deleted!")
                        ?.setContentText(foodList[position].name + " has been deleted!")
                        ?.setConfirmText("OK")
                        ?.setConfirmClickListener(null)
                        ?.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)

                    update(iD)
                    foodList.removeAt(position)
                    notifyItemRemoved(position)
                }
                .setCancelButton("Cancel") { sweetAlertDialog: SweetAlertDialog? ->
                    sweetAlertDialog?.dismissWithAnimation()
                }
                .show()

//            val dialogBuilder = AlertDialog.Builder(activity)
//
//            dialogBuilder.setMessage("Do you really want to delete "+ foodList[position].name +" ?")
//                .setCancelable(false)
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .setPositiveButton("Proceed") { dialog, id -> update(iD)
//                    foodList.removeAt(position)
//                    notifyItemRemoved(position)
//                }
//                .setNegativeButton("Cancel", DialogInterface.OnClickListener {
//                        dialog, id -> dialog.cancel()
//                })
//
//            val alert = dialogBuilder.create()
//            alert.setTitle(R.string.dialogTitle)
//            alert.show()
        }
    }

    inner class FoodViewHolder(itemView: View, viewType: Int) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.fruitName
        val price = itemView.fruitPrice
        val image = itemView.fruitImage
        val editIconLayout = itemView.editIconLayout
        val deleteIconLayout = itemView.deleteIconLayout
    }

    private fun update(id: String) {

        Log.i("Delete Id of ", id)
        val retrofit = Retrofit.Builder().baseUrl(context.resources.getString(R.string.serverURL))
            .addConverterFactory(
                GsonConverterFactory.create()
            ).build()

        val api = retrofit.create(ServerApi::class.java)

        val call = api.deleteTunda(id)

        val gson = Gson()

        call.enqueue(object : Callback<MatundaResponse> {

            override fun onResponse(
                call: Call<MatundaResponse>,
                response: Response<MatundaResponse>
            ) {
                Log.i("ResponseString LF", gson.toJson(response.body()))

                if (response.isSuccessful) {
                    //if (response.body()?.status!!){
//                        val intent = Intent(this@ListFruitsAdapter, ListFruits::class.java)
//                        startActivity(intent)

                    mData = ArrayList()

                    mData = response.body()?.matunda!!

                    lfAdapter = ListFruitsAdapter(mData, context, activity)

                    // lfAdapter.notifyItemRemoved(pos)

                    // }

//                    response.body()?.matunda
                } else {

                }
            }

            override fun onFailure(call: Call<MatundaResponse>, t: Throwable) {
                Log.i("ResponseFailure1", t.message)
            }

        })

    }


}