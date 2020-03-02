package com.example.rxandroid.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rxandroid.model.Foody
import com.example.rxandroid.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_food2.view.*

class FoodyAdapter(val foodyList:ArrayList<Foody>, val context: Context): RecyclerView.Adapter<FoodyAdapter.FoodyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodyViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val view: View = layoutInflater.inflate(R.layout.item_food2,parent,false)
        return FoodyViewHolder(view,viewType)
    }

    override fun getItemCount(): Int {
        return foodyList.size
    }

    override fun onBindViewHolder(holder: FoodyViewHolder, position: Int) {
        holder.name.text = foodyList[position].name
        holder.price.text = foodyList[position].price
        Picasso.get().load(context.resources.getString(R.string.imageFoodsURL)+foodyList[position].image).into(holder.image)
    }

    inner class FoodyViewHolder(itemView: View, viewType: Int): RecyclerView.ViewHolder(itemView){
        val name = itemView.fruitName
        val price = itemView.fruitPrice
        val image = itemView.fruitImage
    }
}