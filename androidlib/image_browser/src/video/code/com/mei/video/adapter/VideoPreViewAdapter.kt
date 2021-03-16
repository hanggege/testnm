package com.mei.video.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.mei.picker.R
import com.mei.video.model.VideoMediaEntity

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-09-20
 */
internal class VideoPreViewAdapter(val context: Context?, val list: List<VideoMediaEntity>, val itemClick: () -> Unit) : RecyclerView.Adapter<VideoPreViewAdapter.VideoPreTextureHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoPreTextureHolder {
        return VideoPreTextureHolder(LayoutInflater.from(context).inflate(R.layout.picker_video_pre_item, parent, false))
    }

    private var mVideoView: VideoView? = null
    override fun getItemCount(): Int = list.size

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: VideoPreTextureHolder, position: Int) {
        val videoView = holder.videoView
        mVideoView = videoView
        videoView.stopPlayback()
        videoView.setVideoPath(list[position].path)
        playOrPauseUi(true, holder)
        videoView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    playOrPauseUi(!videoView.isPlaying, holder)
                }
            }
            true
        }
    }

    private fun playOrPauseUi(play: Boolean, holderView: VideoPreTextureHolder) {
        if (play) {//播放
            holderView.videoView.start()
            holderView.videoPause.visibility = View.GONE
        } else {//暂停
            holderView.videoView.pause()
            holderView.videoPause.visibility = View.VISIBLE
        }
    }

    class VideoPreTextureHolder(var itemV: View) : RecyclerView.ViewHolder(itemV) {
        val videoView: VideoView by lazy { itemV.findViewById<VideoView>(R.id.pre_video_view) }
        val videoPause: ImageView by lazy { itemV.findViewById<ImageView>(R.id.short_video_play_or_pause_iv) }

    }


    fun onPause() {
        mVideoView?.pause()
        Log.i("videoView", "暂停播放")
    }

    fun onDestroyView() {
        Log.i("videoView", "释放资源")
        mVideoView?.stopPlayback()
    }


}