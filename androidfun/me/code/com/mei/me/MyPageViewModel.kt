package com.mei.me

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.net.model.user.MyPageInfo

/**
 * Created by hang on 2020/8/15.
 */
class MyPageViewModel(private val repository: MyPageRepository) : ViewModel() {

    val myPageLiveData: MutableLiveData<List<Any>> = MutableLiveData()

    val isLoading: MutableLiveData<Boolean> = MutableLiveData()

    val startLiveData: MutableLiveData<Any?> = MutableLiveData()

    fun fetchMyPageInfo() {
        repository.fetchMyPageInfo(myPageLiveData, isLoading, startLiveData)
    }

    fun changeNickname(nickName: String) {
        (myPageLiveData.value?.get(0) as? MyPageInfo.Info)?.nickname = nickName
    }

}