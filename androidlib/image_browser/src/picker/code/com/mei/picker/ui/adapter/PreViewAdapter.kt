package com.mei.picker.ui.adapter

import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.mei.base.dip
import com.mei.base.screenHeight
import com.mei.base.screenWidth
import com.mei.picker.R
import com.mei.picker.model.PhotoItem
import java.io.File

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-09-20
 */
internal class PreViewAdapter(val list: List<PhotoItem>, val itemClick: () -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val imgView = PhotoView(parent.context).apply {
            scaleType = ImageView.ScaleType.FIT_CENTER
            layoutParams = ViewGroup.LayoutParams(screenWidth + context.dip(10), screenHeight)
            setPadding(context.dip(5), 0, context.dip(5), 0)
            setOnPhotoTapListener { _, _, _ ->
                itemClick()
            }
        }
        return object : RecyclerView.ViewHolder(imgView) {}
    }


    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder.itemView as? ImageView)?.apply {
            Glide.with(this)
                    .load(File(list[position].imagePath))
                    .optionalFitCenter()
                    .placeholder(R.drawable.default_error)
                    .error(R.drawable.default_error)
                    .into(this)
        }
    }


}