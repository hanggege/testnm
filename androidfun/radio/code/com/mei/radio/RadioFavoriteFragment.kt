package com.mei.radio

import android.annotation.SuppressLint
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
import com.mei.radio.adapter.RadioFavoriteAdapter
import com.mei.wood.R
import com.net.model.radio.RadioAudioInfo
import kotlinx.android.synthetic.main.dialog_radio_list.*

/**
 * MyLikeFragment
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-09-22
 */
class RadioFavoriteFragment : BottomDialogFragment() {

    val audioList = arrayListOf<RadioAudioInfo>()
    var back: (audio: RadioAudioInfo) -> Unit = { }
    val adapter = RadioFavoriteAdapter(audioList).apply {
        setOnItemClickListener { _, _, position ->
            selectedPos = position
            notifyDataSetChanged()
            dialog_radio_rv.post {
                back(audioList[position])
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

    @SuppressLint("SetTextI18n")
    private fun initView() {
        dialog_radio_rv.adapter = adapter
        dialog_radio_rv.addItemDecoration(LineItemDecoration().apply {
            color = ContextCompat.getColor(dialog_radio_rv.context, R.color.color_FFEBEBEB)
            marginHorizontal = dip(15)
            dividerHeight = dip(0.5f)
        })
        dialog_radio_title.paint.isFakeBoldText = true
        dialog_radio_title.text = "我喜欢的（${audioList.size}）"
        dialog_radio_close.setOnClickListener {
            dialog?.dismiss()
        }
    }

}

fun FragmentActivity.showRadioFavoriteDialog(favoriteList: List<RadioAudioInfo>, curAudio: Int = -1, back: (audio: RadioAudioInfo) -> Unit = { }): RadioFavoriteFragment {
    val dialog = RadioFavoriteFragment().apply {
        audioList.clear()
        favoriteList.forEachIndexed { index, radioAudioInfo ->
            audioList.add(radioAudioInfo.apply { serialNum = String.format("%02d", index + 1) })
            if (curAudio == radioAudioInfo.audioId) adapter.selectedPos = index
        }
        this.back = back
    }
    dialog.show(supportFragmentManager, "RadioFavoriteFragment")
    return dialog
}