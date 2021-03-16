package com.mei.me

import androidx.lifecycle.MutableLiveData
import com.joker.im.custom.chick.ServiceInfo
import com.joker.im.custom.chick.SplitText
import com.mei.me.holder.MineFollowAnchorListData
import com.mei.me.holder.MineMutableData
import com.mei.me.holder.MineServiceListData
import com.mei.orc.Cxt
import com.mei.orc.http.retrofit.RetrofitClient
import com.mei.orc.john.model.JohnUser
import com.mei.orc.util.save.getObjectMMKV
import com.mei.orc.util.save.putMMKV
import com.mei.wood.R
import com.mei.wood.extensions.executeToastKt
import com.net.model.user.MyPageInfo
import com.net.model.user.MyPageInfo.MeTabItemPosition
import com.net.network.chick.user.MyPageRequest

/**
 * Created by hang on 2020/8/17.
 */
class MyPageRepository(private val apiSpiceMgr: RetrofitClient) {

    private val unLoginDataListC = arrayListOf(
            MyPageInfo.MeTabItem("", "https://img.meidongli.net/0/2020/07/22/my/page/icon/icon_me_gift.png", "", SplitText(Cxt.getStr(R.string.invite_for_gift)), SplitText(), true, "").apply {
                position = MeTabItemPosition.SINGLE_TOP
            }
    )
    private val unLoginDataListD = arrayListOf(
            MyPageInfo.MeTabItem("dove://setting", "https://img.meidongli.net/0/2020/07/22/my/page/icon/icon_me_setting.png", "", SplitText(Cxt.getStr(R.string.setting)), SplitText(), false, "").apply {
                position = MeTabItemPosition.SINGLE_BOTTOM
            }
    )

    /**
     * 未登录 或者 没网并且没缓存时候的默认数据展示
     */
    private fun getDefaultPageInfo() =
            MyPageInfo().apply {
                info = MyPageInfo.Info()
                mySpecialServices = arrayListOf<ServiceInfo>()
                listC = unLoginDataListC
                listD = unLoginDataListD
                noServiceLabel = MyPageInfo.NoServiceLabel().apply {
                    noServiceLabel = "登录后查看专属服务"
                    showButton = false
                }
            }

    fun fetchMyPageInfo(liveData: MutableLiveData<List<Any>>, isLoading: MutableLiveData<Boolean>, startLiveData: MutableLiveData<Any?>) {
        if (!JohnUser.getSharedUser().hasLogin()) {
            liveData.value = convertPageList(getDefaultPageInfo())
            startLiveData.value = null
        } else {
            isLoading.value = true
            apiSpiceMgr.executeToastKt(MyPageRequest(), success = {
                if (it.data != null) {
                    liveData.value = convertPageList(it.data)
                    startLiveData.value = it.data.startLiveButton
                    JohnUser.getSharedUser().userIDAsString.putMMKV(it.data)
                } else {
                    liveData.value = fetchPageCacheInfo()
                    startLiveData.value = fetchCacheStartLive()
                }
            }, failure = {
                liveData.value = fetchPageCacheInfo()
                startLiveData.value = fetchCacheStartLive()
            }, finish = {
                isLoading.value = false
            })
        }
    }

    private fun fetchPageCacheInfo(): List<Any> {
        val pageInfo = JohnUser.getSharedUser().userIDAsString.getObjectMMKV(MyPageInfo::class.java)
                ?: getDefaultPageInfo()
        return convertPageList(pageInfo)
    }

    private fun fetchCacheStartLive(): Any {
        val pageInfo = JohnUser.getSharedUser().userIDAsString.getObjectMMKV(MyPageInfo::class.java)
                ?: getDefaultPageInfo()
        return pageInfo.startLiveButton
    }

    private fun convertPageList(pageInfo: MyPageInfo): List<Any> {
        val pageList = arrayListOf<Any>()
        pageList.add(pageInfo.info ?: MyPageInfo.Info())
        pageInfo.finance?.let {
            pageList.add(it)
        }
        if (pageInfo.mySpecialServices != null) {
            val noServiceLabel = pageInfo.noServiceLabel ?: MyPageInfo.NoServiceLabel().apply {
                toAllServiceAction = pageInfo.specialServiceAction
                toWebServiceListAction = pageInfo.mySpecialServiceAction
                noServiceLabel = "暂未开通专属服务"
                showButton = true
            }
            pageList.add(MineServiceListData(pageInfo.mySpecialServices, noServiceLabel))
        }
        if (pageInfo.listUser?.isNotEmpty() == true) {
            pageInfo.listUser?.let {
                pageList.add(MineMutableData(setListItemLocation(it), true, MeTabItemPosition.SINGLE_TOP))
            }
        }
        pageInfo.listA?.let {
            pageList.addAll(setListItemLocation(it, MeTabItemPosition.SINGLE_TOP))
        }
        pageInfo.listB?.let {
            pageList.addAll(setListItemLocation(it))
        }
        pageInfo.listC?.let {
            pageList.addAll(setListItemLocation(it))
        }
        pageInfo.listTools?.let {
            pageList.add(MineMutableData(setListItemLocation(it)))
        }
        pageInfo.listD?.let {
            pageList.addAll(setListItemLocation(it, MeTabItemPosition.SINGLE_BOTTOM))
        }
        if (pageInfo.myFollowList.orEmpty().isNotEmpty()) {
            val indexOfFirst = pageList.indexOfFirst { (it as? MyPageInfo.MeTabItem)?.action.orEmpty().contains("my_follow_list") }
            if (indexOfFirst > -1) {
                pageList.add(indexOfFirst + 1, MineFollowAnchorListData(pageInfo.myFollowList))
            }
        }
        return pageList
    }

    private fun setListItemLocation(list: MutableList<MyPageInfo.MeTabItem>,
                                    position: MeTabItemPosition = MeTabItemPosition.UNDEFINED): MutableList<MyPageInfo.MeTabItem> {
        if (list.size == 1) {
            if (position != MeTabItemPosition.UNDEFINED) {
                list[0].position = position
            } else if (list[0].position == null) {
                list[0].position = MeTabItemPosition.SINGLE
            }
            return list
        }
        list.forEachIndexed { index, meTabItem ->
            when (index) {
                0 -> meTabItem.position = MeTabItemPosition.AREA_FIRST
                list.size - 1 -> meTabItem.position = MeTabItemPosition.AREA_LAST
                else -> meTabItem.position = MeTabItemPosition.AREA_MID
            }
        }
        return list
    }
}