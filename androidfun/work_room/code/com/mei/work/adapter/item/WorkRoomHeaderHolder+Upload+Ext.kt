package com.mei.work.adapter.item

import androidx.fragment.app.FragmentActivity
import com.mei.base.upload.MeiUploader
import com.mei.orc.callback.TaskCallback
import com.mei.orc.john.upload.PhotoList
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.image.uriToFile
import com.mei.picker.pickerImage
import com.mei.widget.holder.activity
import com.mei.wood.extensions.executeKt
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.util.logDebug
import com.net.network.chick.friends.HomePageCoverRequest
import com.net.network.chick.friends.WorkListRequest
import com.net.network.works.AddUserResourcesListRequest

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/31
 */


/**
 * 上传封面图片
 */
fun WorkRoomHeaderHolder.uploadHomePageHeadImg() {
    (activity as? FragmentActivity)?.pickerImage(1) { list ->
        list.firstOrNull()?.let {
            showLoading(true)
            MeiUploader(apiSpiceMgr) { key, percent -> logDebug("uploadImg", "$percent   $key") }
                    .uploadImage(list, object : TaskCallback<PhotoList>() {
                        override fun onSuccess(result: PhotoList?) {
                            val photoUri = result?.newPhotos.orEmpty().firstOrNull()?.uri.orEmpty()
                            if (photoUri.isNotEmpty()) {
                                apiSpiceMgr.executeToastKt(HomePageCoverRequest(photoUri), success = {
                                    pagerResult.info?.homepageCover = photoUri
                                    refreshAdapter()
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
fun WorkRoomHeaderHolder.uploadImageToQiniu(imgs: List<String>) {
    if (imgs.isNotEmpty()) {
        showLoading(true)
        MeiUploader(apiSpiceMgr) { key, percent -> logDebug("uploadImg", "$percent   $key") }
                .uploadImage(imgs, object : TaskCallback<PhotoList>() {
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
                                //上传成功，去刷新
                                refreshWorks()
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
fun WorkRoomHeaderHolder.uploadVideoToQiniu(videoList: List<String>) {
    videoList.firstOrNull()?.let { _ ->
        UIToast.toast("视频上传中")
        MeiUploader(apiSpiceMgr) { key, percent ->
            logDebug("uploadVideo", "$percent   $key")
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
                        //  上传成功，去刷新
                        refreshWorks()
                    })
                } else {
                    UIToast.toast(activity, "视频上传失败，请重试")
                }
            }

            override fun onFailed(exception: Exception?, errCode: Int, vararg objects: Any?) {
                UIToast.toast(activity, "视频上传失败，请重试")
            }
        })
    }
}

private fun WorkRoomHeaderHolder.refreshWorks() {
    apiSpiceMgr.executeKt(WorkListRequest(pagerResult.info?.userId ?: 0, 0), success = {
        it.data?.let { data ->
            pagerResult.worksCount = data.count
            pagerResult.works?.worksCount = data.count
            pagerResult.works?.let {
                it.worksCount = data.count
                it.nextPageNo = data.list.nextPageNo
                it.hasMore = data.list.hasMore
                it.list = data.list.list
            }
            refreshAdapter()
        }
    })
}