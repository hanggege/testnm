package com.mei.video.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.mei.picker.R
import com.mei.picker.ui.PICKER_RESULT
import com.mei.video.adapter.VideoPreViewAdapter
import com.mei.video.model.VideoMediaEntity
import kotlinx.android.synthetic.main.pre_video_layout.*

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-09-20
 */
internal class VideoPreviewFragment : Fragment() {

    private val imageList = arrayListOf<VideoMediaEntity>()
    private val adapter: VideoPreViewAdapter by lazy { VideoPreViewAdapter(context, imageList) { showPreView(show = false) } }
    var selectBack: (VideoMediaEntity) -> Unit = {}
    var showPre: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.pre_video_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_view.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        recycler_view.adapter = adapter
        PagerSnapHelper().attachToRecyclerView(recycler_view)
        recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    selectedState(selectedIndex())
                }
            }
        })

        pre_back_view.setOnClickListener {
            showPreView(show = false)
            adapter.onPause()
        }
        photo_select_btn.setOnClickListener {
            if (selectedIndex() > -1 && selectedIndex() < imageList.size) {
                val item = imageList[selectedIndex()]
                if (selectedVideo.any { it.id == item.id }) selectedVideo.removeAll { it.id == item.id }
                else putVideo(item)
                selectedState(selectedIndex())
                selectBack(item)
            }
        }
        showPreView(show = false)

        /**在预览界面也能直接确认选择图片返回*/
        commit.setOnClickListener {
            /** 返回选择的图片 **/
            activity?.setResult(Activity.RESULT_OK, Intent().apply {
                val list = arrayListOf<VideoMediaEntity>()
                selectedVideo.forEach {
                    list.add(it)
                }
                putParcelableArrayListExtra(PICKER_RESULT, list)
            })
            activity?.finish()
        }
    }

    fun selectedState(index: Int) {
        val selected = selectedVideo.any { it.id == imageList[index].id }
        photo_select_btn.setImageResource(if (selected) R.drawable.mul_pictures_selected else R.drawable.mul_pictures_unselected)
        photo_select_btn.isSelected = selected
        photo_per_tv.text = "${index + 1}/${imageList.size}"
    }

    private fun selectedIndex() = (recycler_view.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

    fun showPreView(list: List<VideoMediaEntity> = arrayListOf(), index: Int = 0, show: Boolean = true) {
        view?.let {
            showPre = show
            if (show) {
                it.y = it.height.toFloat()
                this.imageList.clear()
                this.imageList.addAll(list)
                adapter.notifyDataSetChanged()
                recycler_view.scrollToPosition(index)
                selectedState(index)
                it.bringToFront()
                it.animate().translationYBy(it.height.toFloat()).translationY(0f).setDuration(300).start()
            } else {
                it.animate().translationYBy(0f).translationY(it.height.toFloat()).setDuration(300).start()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        adapter.onPause()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter.onDestroyView()
    }


}