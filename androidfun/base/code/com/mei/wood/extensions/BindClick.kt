package com.mei.wood.extensions

import android.app.Activity
import android.app.Dialog
import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import androidx.fragment.app.Fragment as SupportFragment

/**
 * Created by joker on 2018/3/20.
 */
fun Activity.bindClick(@IdRes vararg ids: Int, clickListener: (Int) -> Unit) {
    ids.forEach {
        val t = findViewById<View>(it)
        t.setOnClickListener { _ -> clickListener(it) }
    }
}

fun SupportFragment.bindClick(@IdRes vararg ids: Int, clickListener: (Int) -> Unit) {
    ids.forEach {
        view!!.findViewById<View>(it).setOnClickListener { _ -> clickListener(it) }
    }
}

fun View.bindClick(@IdRes vararg ids: Int, clickListener: (Int) -> Unit) {
    ids.forEach {
        findViewById<View>(it).setOnClickListener { _ -> clickListener(it) }
    }
}

fun RecyclerView.ViewHolder.bindClick(@IdRes vararg ids: Int, clickListener: (Int) -> Unit) {
    ids.forEach {
        itemView.findViewById<View>(it).setOnClickListener { _ -> clickListener(it) }
    }
}

fun Dialog.bindClick(@IdRes vararg ids: Int, clickListener: (Int) -> Unit) {
    ids.forEach {
        findViewById<View>(it).setOnClickListener { _ -> clickListener(it) }
    }
}
