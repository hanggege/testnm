package com.mei.mentor_home_page.adapter.item

import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.flyco.roundview.RoundTextView
import com.mei.base.common.MENTOR_PAGE_CHANGED
import com.mei.base.upload.MeiUploader
import com.mei.mentor_home_page.model.WorkTitle
import com.mei.orc.callback.TaskCallback
import com.mei.orc.event.postAction
import com.mei.orc.john.upload.PhotoList
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.image.uriToFile
import com.mei.picker.pickerImage
import com.mei.video.pickerVideo
import com.mei.widget.holder.activity
import com.mei.widget.holder.quickAdapter
import com.mei.wood.R
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.util.logDebug
import com.net.model.chick.friends.ProductBean
import com.net.network.works.AddUserResourcesListRequest

/**
 *
 * @author Created by lenna on 2020/5/23
 * 作品title item
 */
class MentorHomePageWorkTitleHolder(view: View) : MentorBaseHolder(view) {
    private var iconVideo: RoundTextView = getView(R.id.mentor_home_page_icon_upload_video)
    private var iconPic: RoundTextView = getView(R.id.mentor_home_page_icon_upload_pic)

    override fun refresh(item: Any) {
        if (item is WorkTitle) {
            item.apply {
                iconPic.isVisible = isSelf
                iconVideo.isVisible = isSelf
            }
        }
        iconPic.setOnClickListener {
            (activity as? FragmentActivity)?.pickerImage(9) { uploadImageToQiniu(it) }
        }
        iconVideo.setOnClickListener {
            (activity as? FragmentActivity)?.pickerVideo(1) { uploadVideoToQiniu(it.mapNotNull { it.path }) }
        }
    }


    /**
     * 上传图片到七牛
     */
    private fun uploadImageToQiniu(imgs: List<String>) {
        if (imgs.isNotEmpty()) {
            showLoading(true)
            MeiUploader(apiSpiceMgr).uploadImage(imgs, object : TaskCallback<PhotoList>() {
                override fun onSuccess(result: PhotoList?) {
                    if (result?.newPhotos.orEmpty().isNotEmpty()) {
                        val list = result?.newPhotos.orEmpty().mapNotNull {
                            AddUserResourcesListRequest.ResourcesRequest.ResourcesData().apply {
                                worksType = "0"
                                url = it.uri
                                coverUrl = it.uri
                            }
                        }
                        apiSpiceMgr.executeToastKt(AddUserResourcesListRequest(AddUserResourcesListRequest.ResourcesRequest(list)), success = {
                            postAction(MENTOR_PAGE_CHANGED, true)
                        }, finish = { showLoading(false) })
                    } else {
                        showLoading(false)
                        UIToast.toast(activity, "图片上传失败，请重试")
                    }
                }

                override fun onFailed(exception: Exception?, errCode: Int, vararg objects: Any?) {
                    showLoading(false)
                    UIToast.toast(activity, "图片上传失败，请重试")
                }
            })
        }
    }

    /**
     * 上传视频到七牛
     */
    @Suppress("UNCHECKED_CAST")
    private fun uploadVideoToQiniu(videoList: List<String>) {
        videoList.firstOrNull()?.let { videoPath ->
            UIToast.toast("视频上传中")
            val data = ProductBean().apply {
                worksType = 1
                url = videoPath
                cover = videoPath
                progress = 0.0
            }
            var lastIndex = quickAdapter?.data.orEmpty().indexOfLast { (it as? ProductBean)?.isTop == 1 }
            if (lastIndex == -1) {
                lastIndex = quickAdapter?.data.orEmpty().indexOfFirst { it is ProductBean }
            }
            if (lastIndex > -1) {
                lastIndex++
                @Suppress("UNCHECKED_CAST")
                (quickAdapter?.data as? MutableList<Any>)?.add(lastIndex, data)
                quickAdapter?.notifyDataSetChanged()
            }
            MeiUploader(apiSpiceMgr) { key, percent ->
                logDebug("uploadVideo", "$percent   $key")
                data.progress = percent * 100
                if (lastIndex > -1) quickAdapter?.notifyItemChanged(lastIndex, "notifyItemChanged")
            }.uploadVideo(uriToFile(videoList), videoList, object : TaskCallback<PhotoList>() {
                override fun onSuccess(result: PhotoList?) {
                    if (result?.newPhotos.orEmpty().isNotEmpty()) {
                        val list = result?.newPhotos.orEmpty().mapNotNull {
                            AddUserResourcesListRequest.ResourcesRequest.ResourcesData().apply {
                                worksType = "1"
                                url = it.uri
                                coverUrl = it.uri
                            }
                        }
                        apiSpiceMgr.executeToastKt(AddUserResourcesListRequest(AddUserResourcesListRequest.ResourcesRequest(list)), success = {
                            postAction(MENTOR_PAGE_CHANGED, true)
                        })
                    } else {
                        UIToast.toast(activity, "视频上传失败，请重试")
                        (quickAdapter?.data as? MutableList<Any>)?.remove(data)
                        quickAdapter?.notifyDataSetChanged()
                    }
                }

                override fun onFailed(exception: Exception?, errCode: Int, vararg objects: Any?) {
                    UIToast.toast(activity, "视频上传失败，请重试")
                    (quickAdapter?.data as? MutableList<Any>)?.remove(data)
                    quickAdapter?.notifyDataSetChanged()
                }

                override fun onCanceled() {
                    super.onCanceled()
                    UIToast.toast(activity, "视频上传失败，请重试")
                    (quickAdapter?.data as? MutableList<Any>)?.remove(data)
                    quickAdapter?.notifyDataSetChanged()
                }
            })
        }
    }

}