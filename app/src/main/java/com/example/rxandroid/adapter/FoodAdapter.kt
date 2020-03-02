package com.example.rxandroid.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rxandroid.R
import com.example.rxandroid.model.Tunda
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_food.view.*

class FoodAdapter (val foodList:ArrayList<Tunda>, val context: Context): RecyclerView.Adapter<FoodAdapter.FoodViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val view:View = layoutInflater.inflate(R.layout.item_food,parent,false)
        return FoodViewHolder(view,viewType)
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.name.text = foodList[position].name
        Picasso.get().load(context.resources.getString(R.string.imageFruitsURL)+foodList[position].image).into(holder.image)
    }

    inner class FoodViewHolder(itemView: View, viewType: Int): RecyclerView.ViewHolder(itemView){
        val name = itemView.foodName
        val image = itemView.foodImage
    }
}