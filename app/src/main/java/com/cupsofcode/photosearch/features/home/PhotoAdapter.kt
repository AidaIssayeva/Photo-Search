package com.cupsofcode.photosearch.features.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.cupsofcode.photosearch.R
import com.cupsofcode.photosearch.repository.model.Photo


class PhotoAdapter(private val listener: PhotoAdapter.Listener) :
    Adapter<PhotoAdapter.ViewHolder>() {

    private val photos = arrayListOf<Photo>()

    interface Listener {
        fun onPhotoClicked(photo: Photo)
    }

    class ViewHolder(itemView: View, private val clickListener: Listener) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView
        private val titleView: TextView

        init {
            imageView = itemView.findViewById(R.id.img)
            titleView = itemView.findViewById(R.id.title)

        }

        fun bind(photo: Photo) {
            titleView.text = photo.title
            Glide.with(imageView)
                .load(photo.imageUri)
                .placeholder(R.drawable.background_empty)
                .error(R.drawable.background_error)
                .into(imageView)
            itemView.setOnClickListener {
                clickListener.onPhotoClicked(photo)
            }
        }
    }

    fun addPhotos(list: List<Photo>) {
        photos.clear()
        photos.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_photo, parent, false)

        return ViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(photos[position])

    }

    override fun getItemCount(): Int {
        return photos.size
    }
}