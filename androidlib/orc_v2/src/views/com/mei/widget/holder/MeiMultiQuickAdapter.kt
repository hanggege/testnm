package com.mei.widget.holder

import android.app.Activity
import android.content.ContextWrapper
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * 多类型布局，适用于类型较少，业务不复杂的场景，便于快速使用。
 * data[T]必须实现[MultiItemEntity]
 *
 * 如果数据类无法实现[MultiItemEntity]，请使用[BaseDelegateMultiAdapter]
 * 如果类型较多，为了方便隔离各类型的业务逻辑，推荐使用[BaseProviderMultiAdapter]
 *
 * @param T : MultiItemEntity
 * @param VH : BaseViewHolder
 * @constructor
 */
abstract class MeiMultiQuickAdapter<T, VH : BaseViewHolder>(data: MutableList<T>)
    : BaseQuickAdapter<T, VH>(0, data) {

    override fun getDefItemViewType(position: Int): Int {
        require(true) { "请重写该方法" }
        return 0
    }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): VH {
        require(true) { "请重写该方法" }
        return createBaseViewHolder(parent, 0)
    }
}

fun BaseViewHolder.setVisibleAndGone(@IdRes viewId: Int, isVisible: Boolean): BaseViewHolder {
    setGone(viewId, !isVisible)
    return this
}

fun BaseViewHolder.addOnClickListener(@IdRes vararg viewIds: Int): BaseViewHolder {
    for (viewId in viewIds) {
        quickAdapter?.addChildClickViewIds(viewId)
    }
    val viewHolder = this
    quickAdapter?.apply {
        getOnItemChildClickListener()?.let {
            for (id in getChildClickViewIds()) {
                viewHolder.itemView.findViewById<View>(id)?.let { childView ->
                    if (!childView.isClickable) {
                        childView.isClickable = true
                    }
                    childView.setOnClickListener { v ->
                        var position = viewHolder.bindingAdapterPosition
                        if (position == RecyclerView.NO_POSITION) {
                            return@setOnClickListener
                        }
                        position -= headerLayoutCount
                        it.onItemChildClick(this, v, position)
                    }
                }
            }
        }
    }
    return this
}

fun BaseViewHolder.addOnLongClickListener(@IdRes vararg viewIds: Int): BaseViewHolder {
    for (viewId in viewIds) {
        quickAdapter?.addChildLongClickViewIds(viewId)
    }
    val viewHolder = this
    quickAdapter?.apply {
        getOnItemChildLongClickListener()?.let {
            for (id in getChildLongClickViewIds()) {
                viewHolder.itemView.findViewById<View>(id)?.let { childView ->
                    if (!childView.isLongClickable) {
                        childView.isLongClickable = true
                    }
                    childView.setOnLongClickListener { v ->
                        var position = viewHolder.bindingAdapterPosition
                        if (position == RecyclerView.NO_POSITION) {
                            return@setOnLongClickListener false
                        }
                        position -= headerLayoutCount
                        it.onItemChildLongClick(this, v, position)
                    }
                }
            }
        }
    }
    return this
}

inline var BaseViewHolder.quickAdapter: BaseQuickAdapter<*, *>?
    get() = bindingAdapter as? BaseQuickAdapter<*, *>
    set(_) {}

inline var RecyclerView.ViewHolder.activity: Activity?
    get() = if (itemView.context is Activity) itemView.context as? Activity else (itemView.context as? ContextWrapper)?.baseContext as? Activity
    set(_) {}