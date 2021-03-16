package com.mei.work.ui

import com.mei.base.upload.MeiUploader
import com.mei.orc.callback.TaskCallback
import com.mei.orc.john.upload.PhotoList
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.image.uriToFile
import com.mei.picker.pickerImage
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.util.logDebug
import com.net.model.chick.friends.ProductBean
import com.net.network.chick.friends.HomePageCoverRequest
import com.net.network.works.AddUserResourcesListRequest

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/30
 */

/**
 * 上传封面图片
 */
fun WorkRoomMainFragment.uploadHomePageHeadImg() {
    activity?.pickerImage(1) { list ->
        list.firstOrNull()?.let {
            showLoading(true)
            MeiUploader(apiSpiceMgr) { key, percent -> logDebug("uploadImg", "$percent   $key") }
                    .uploadImage(list, object : TaskCallback<PhotoList>() {
                        override fun onSuccess(result: PhotoList?) {
                            val photoUri = result?.newPhotos.orEmpty().firstOrNull()?.uri.orEmpty()
                            if (photoUri.isNotEmpty()) {
                                apiSpiceMgr.executeToastKt(HomePageCoverRequest(photoUri), success = {
//                            mentor_bg_iv.glideDisplay(photoUri, pagerResult.info?.userInfo?.gender.genderAvatar())
                                    // TODO: joker 2020/7/30 需要替换第一张的封面图片
                                }, finish = {
                                    showLoading(false)
                                })
                            } else {
                                showLoading(false)
                                UIToast.toast(activity, "封面上传失败，请重试")
                            }
                        }

                        override fun onFailed(exception: Exception?, errCode: Int, vararg objects: Any?) {
                            showLoading(false)
                            UIToast.toast(activity, "封面上传失败，请重试")
                        }
                    })
        }
    }
}

/**
 * 上传图片到七牛
 */
fun WorkRoomMainFragment.uploadImageToQiniu(imgs: List<String>) {
    if (imgs.isNotEmpty()) {
        showLoading(true)
        MeiUploader(apiSpiceMgr) { key, percent -> logDebug("uploadImg", "$percent   $key") }.uploadImage(imgs, object : TaskCallback<PhotoList>() {
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
                        // TODO: joker 2020/7/30 上传成功，去刷新 
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
fun WorkRoomMainFragment.uploadVideoToQiniu(videoList: List<String>) {
    videoList.firstOrNull()?.let { videoPath ->
        UIToast.toast("视频上传中")
        val data = ProductBean().apply {
            worksType = 1
            url = videoPath
            cover = videoPath
            progress = 0.0
        }
        var lastIndex = mAdapter.data.indexOfLast { (it as? ProductBean)?.isTop == 1 }
        if (lastIndex == -1) {
            lastIndex = mAdapter.data.indexOfFirst { it is ProductBean }
        }
        if (lastIndex > -1) {
            lastIndex++
            @Suppress("UNCHECKED_CAST")
            (mAdapter.data as? MutableList<Any>)?.add(lastIndex, data)
            mAdapter.notifyDataSetChanged()
        }
        MeiUploader(apiSpiceMgr) { key, percent ->
            logDebug("uploadVideo", "$percent   $key")
            data.progress = percent * 100
            if (lastIndex > -1) mAdapter.notifyItemChanged(lastIndex, "notifyItemChanged")
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
                        // TODO: joker 2020/7/30 上传成功，去刷新 
                    })
                } else {
                    UIToast.toast(activity, "视频上传失败，请重试")
                    (mAdapter.data as? MutableList<Any>)?.remove(data)
                    mAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailed(exception: Exception?, errCode: Int, vararg objects: Any?) {
                UIToast.toast(activity, "视频上传失败，请重试")
                (mAdapter.data as? MutableList<Any>)?.remove(data)
                mAdapter.notifyDataSetChanged()
            }
        })
    }
}
