package com.cupsofcode.photosearch.features.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cupsofcode.photosearch.R


class SearchListAdapter constructor(private val listener: Listener) :
    RecyclerView.Adapter<SearchListAdapter.ViewHolder>() {
    private val searchList = arrayListOf<String>()

    interface Listener {
        fun searchItemClicked(query: String)
        fun closeClicked(query: String)
    }

    class ViewHolder(itemView: View, private val listener: Listener) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        private val textView: TextView
        private val iconView: ImageView

        init {
            textView = itemView.findViewById<TextView>(R.id.textView)
            iconView = itemView.findViewById<ImageView>(R.id.close)
        }

        fun bind(query: String) {
            textView.text = query
            itemView.setOnClickListener {
                listener.searchItemClicked(query)
            }
            iconView.setOnClickListener {
                listener.closeClicked(query)
            }

        }
    }

    fun addAll(list: List<String>) {
        searchList.clear()
        searchList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search, parent, false)

        return SearchListAdapter.ViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: SearchListAdapter.ViewHolder, position: Int) {
        holder.bind(searchList[position])
    }

    override fun getItemCount(): Int {
        return searchList.size
    }
}