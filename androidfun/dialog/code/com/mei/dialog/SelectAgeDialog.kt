package com.mei.dialog;

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.FragmentActivity
import com.mei.dialog.adapter.RangeOfAgeAdapter
import com.mei.dialog.adapter.RangeOfAgeItemDecoration
import com.mei.orc.dialog.BottomDialogFragment
import com.mei.orc.util.handler.GlobalUIHandler
import com.mei.wood.R
import kotlinx.android.synthetic.main.view_select_age.*

/**
 * SelectAgeDialog
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-08-18
 */
fun FragmentActivity.showSelectAgeDialog(list: MutableList<String>, age: String, callBack: (type: Int, extra: String?) -> Unit = { _, _ -> }) {
    SelectAgeDialog().apply {
        this.callBack = callBack
        this.age = age
        setAgeList(list)
    }.show(supportFragmentManager, "SelectAgeDialog")
}


class SelectAgeDialog : BottomDialogFragment() {
    var callBack: (type: Int, extra: String?) -> Unit = { _, _ -> }
    var age: String = ""

    private val years: MutableList<String> = arrayListOf()
    private val mAdapter by lazy {
        RangeOfAgeAdapter(years).apply {
            setOnItemClickListener { _, _, position ->
                dimiss()
                callBack(position, years[position])
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.view_select_age, container, false)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

        dialog_sel_age_cancel.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

    private fun initView() {
        dialog_sel_age_rv.adapter = mAdapter
        dialog_sel_age_rv.addItemDecoration(RangeOfAgeItemDecoration())
    }

    fun setAgeList(list: MutableList<String>) {
        years.clear()
        years.addAll(list)
        mAdapter.selectedPos = years.indexOf(age)
        mAdapter.notifyDataSetChanged()
    }

    private fun dimiss() {
        GlobalUIHandler.schedule({
            dismissAllowingStateLoss()
        }, 300)
    }
}