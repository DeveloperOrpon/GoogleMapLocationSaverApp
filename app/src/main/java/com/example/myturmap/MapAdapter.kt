package com.example.myturmap

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentProvider
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myturmap.model.UserMap

class MapAdapter(val context: Context,val UserMap: List<UserMap>,val onClickListener: OnClickListener) : RecyclerView.Adapter<MapAdapter.ViewHolder>() {

    interface OnClickListener{
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val veiw=LayoutInflater.from(context).inflate(R.layout.item_user_map,parent,false)
        return ViewHolder(veiw)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userMap=UserMap[position]
        var textViewTile=holder.itemView.findViewById<TextView>(R.id.tvUserMap)
        textViewTile.text="${position + 1}. ${userMap.title}"
        holder.itemView.setOnClickListener {
            onClickListener.onItemClick(position)
        }
    }

    override fun getItemCount()=UserMap.size

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
}
