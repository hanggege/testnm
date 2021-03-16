package com.mei.base.util.viewmodel

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.mei.base.network.holder.SpiceHolder
import com.mei.me.MyPageRepository
import com.mei.me.MyPageViewModel

/**
 * Created by hang on 2020/8/24.
 */
@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val owner: SavedStateRegistryOwner,
                       defaultArgs: Bundle? = null) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T =
            with(modelClass) {
                when {
                    isAssignableFrom(MyPageViewModel::class.java) && owner is SpiceHolder -> {
                        MyPageViewModel(MyPageRepository(owner.apiSpiceMgr))
                    }
                    else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            } as T
}
