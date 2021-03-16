package com.mei.picker.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mei.base.dip
import com.mei.picker.R
import com.mei.picker.model.PhotoAlbum
import java.io.File

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-09-19
 */
internal class FolderPickerAdapter(val list: List<PhotoAlbum>, val back: (PhotoAlbum) -> Unit)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.folder_line_item, parent, false)
        return object : RecyclerView.ViewHolder(itemView) {}
    }

    override fun getItemCount(): Int = list.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val album = list[position]
        holder.itemView.findViewById<TextView>(R.id.album_name_tv).text = album.bucketName
        holder.itemView.findViewById<TextView>(R.id.album_count_tv)?.apply {
            text = String.format(resources.getString(R.string.image_count_placeholder), album.imageList.size)
        }
        holder.itemView.findViewById<ImageView>(R.id.album_logo_img)?.apply {
            Glide.with(this)
                    .load(File(album.imageList.firstOrNull()?.imagePath ?: ""))
                    .centerCrop()
                    .error(R.drawable.default_error)
                    .placeholder(R.drawable.default_error)
                    .override(this.context.dip(50), this.context.dip(50))
                    .into(this)
        }
        holder.itemView.setOnClickListener { back(album) }
    }
}