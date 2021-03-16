package com.mei.picker.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mei.base.dip
import com.mei.picker.R
import com.mei.picker.model.PhotoItem
import com.mei.picker.ui.putPhoto
import com.mei.picker.ui.selectedPhotos
import java.io.File

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-09-19
 */
internal class ImagePickerAdapter(val list: ArrayList<PhotoItem?>, val itemClick: (Int, PhotoItem?) -> Unit) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.picker_grid_item, parent, false)
        return object : RecyclerView.ViewHolder(itemView) {}
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        val selected = selectedPhotos.any { it.imageId == item?.imageId }
        holder.itemView.setBackgroundColor(Color.parseColor(if (item == null) "#F0F0F0" else "#F8F8F8"))
        holder.itemView.findViewById<View>(R.id.camera_image).visibility = if (item == null) View.VISIBLE else View.GONE
        holder.itemView.findViewById<ImageView>(R.id.show_image_view)?.apply {
            visibility = if (item != null) View.VISIBLE else View.GONE
            if (selected) setColorFilter(Color.parseColor("#88000000"))
            else colorFilter = null
            val padding = context.dip(5)
            setPadding(padding, 0, if (position % 3 < 2) 0 else padding, padding)
            Glide.with(this)
                    .load(File(item?.imagePath ?: ""))
                    .placeholder(R.drawable.default_error)
                    .error(R.drawable.default_error)
                    .centerCrop()
                    .override(this.context.dip(300), this.context.dip(300))
                    .into(this)
        }
        holder.itemView.findViewById<ImageView>(R.id.select_state_img)?.apply {
            visibility = if (item == null) View.GONE else View.VISIBLE
            setImageResource(if (selected) R.drawable.mul_pictures_selected else R.drawable.mul_pictures_unselected)
            setOnClickListener {
                if (selectedPhotos.any { it.imageId == item?.imageId }) {
                    selectedPhotos.removeAll { it.imageId == item?.imageId }
                    this@ImagePickerAdapter.notifyItemChanged(position)
                } else if (item != null && putPhoto(item)) {
                    this@ImagePickerAdapter.notifyItemChanged(position)
                } else if (selectedPhotos.size <= 9 && item != null && putPhoto(item)) {
                    this@ImagePickerAdapter.notifyItemChanged(position)
//                    Toast.makeText(context,item?.imagePath,Toast.LENGTH_SHORT).show()
                }

            }
        }
        holder.itemView.setOnClickListener { itemClick(position, item) }
    }


}