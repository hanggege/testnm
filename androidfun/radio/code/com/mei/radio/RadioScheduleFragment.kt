package com.mei.radio

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentActivity
import com.mei.orc.dialog.BottomDialogFragment
import com.mei.orc.ext.dip
import com.mei.orc.ext.screenHeight
import com.mei.orc.ext.screenWidth
import com.mei.radio.adapter.LineItemDecoration
import com.mei.radio.adapter.RadioScheduleAdapter
import com.mei.wood.R
import com.net.model.radio.RadioSchedulingInfo
import kotlinx.android.synthetic.main.dialog_radio_list.*

/**
 * RadioLikeFragment
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-09-24
 */
class RadioScheduleFragment : BottomDialogFragment() {

    val scheduleList = arrayListOf<RadioSchedulingInfo.RadioScheduleBean>()
    var back: (scheduleInfo : RadioSchedulingInfo.RadioScheduleBean) -> Unit = {  }
    val adapter by lazy {
        RadioScheduleAdapter(scheduleList).apply {
            setOnItemClickListener { _, _, position ->
                selectedPos = position
                notifyDataSetChanged()
                dialog_radio_rv.post {
                    back(scheduleList[position])
                    dialog?.dismiss()
                }
            }
        }
    }

    override fun onSetInflaterLayout() = R.layout.dialog_radio_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.updateLayoutParams {
            width = screenWidth
            height = (screenHeight * 0.7).toInt()
        }
        initView()
    }

    private fun initView() {
        dialog_radio_rv.adapter = adapter
        dialog_radio_rv.addItemDecoration(LineItemDecoration().apply {
            color = ContextCompat.getColor(dialog_radio_rv.context, R.color.color_FFEBEBEB)
            marginHorizontal = dip(15)
            dividerHeight = dip(0.5f)
        })
        dialog_radio_title.paint.isFakeBoldText = true
        dialog_radio_title.text = "定时关闭"
        dialog_radio_close.setOnClickListener {
            dialog?.dismiss()
        }
    }
}

fun FragmentActivity.showRadioScheduleDialog(scheduleInfo: RadioSchedulingInfo, back: (scheduleInfo : RadioSchedulingInfo.RadioScheduleBean) -> Unit = { }): RadioScheduleFragment {
    val dialog = RadioScheduleFragment().apply {
        scheduleList.clear()
        scheduleInfo.list.forEachIndexed { index, radioScheduleBean ->
            if (radioScheduleBean.id == scheduleInfo.defaultTab) {
                adapter.selectedPos = index
            }
            scheduleList.add(radioScheduleBean)
        }
        this.back = back
    }
    dialog.show(supportFragmentManager, "RadioScheduleFragment")
    return dialog
}