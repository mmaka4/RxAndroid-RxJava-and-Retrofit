package com.example.rxandroid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rxandroid.model.Foody
import com.example.rxandroid.R
import kotlinx.android.synthetic.main.list_item.view.*

class ItemAdapter(val itemClick: (position:Int,item: Foody) -> Unit) : RecyclerView.Adapter<ItemViewHolder>() {

    private var items: ArrayList<Foody> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
        ItemViewHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.list_item, parent, false)
        )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
        holder.itemView.setOnClickListener {
            itemClick(position,items[position])
        }
    }

    override fun getItemCount() = items.size

    fun setItems(newItems: ArrayList<Foody>) {
        items = newItems
        notifyDataSetChanged()
    }
}

class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    fun bind(item: Foody) {
        view.list_item_text.text = "${item.name}"
        //view.background = R.d
    }
}