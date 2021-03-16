package com.mei.live.ui.dialog

import android.annotation.SuppressLint
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.annotation.IntRange
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.google.android.material.shape.*
import com.mei.live.manager.genderAvatar
import com.mei.orc.Cxt
import com.mei.orc.ext.dip
import com.mei.orc.ext.screenWidth
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.ui.toast.UIToast
import com.mei.wood.R
import com.mei.wood.cache.requestUserInfo
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.ui.CustomBottomDialogFragment
import com.net.model.chick.evaluation.EvaluationBean
import com.net.network.chick.evaluation.EvaluationRequest
import kotlinx.android.synthetic.main.dialog_live_evaluation.*

/**
 * Created by hang on 2020/11/12.
 * 评价弹窗
 */
fun FragmentActivity.showEvaluationDialog(userId: Int,roomId: String) {
    LiveEvaluationDialog().also {
        it.userId = userId
        it.roomId = roomId
    }.show(supportFragmentManager, LiveEvaluationDialog::class.java.simpleName)
}

class LiveEvaluationDialog : CustomBottomDialogFragment() {

    var userId: Int = 0
    var roomId: String = ""

    private val startEdge by lazy {
        object : TriangleEdgeTreatment(dip(6).toFloat(), false) {
            override fun getEdgePath(length: Float, center: Float, interpolation: Float, shapePath: ShapePath) {
                val centerX = (screenWidth - dip(74)) / 6
                super.getEdgePath(length, centerX.toFloat(), interpolation, shapePath)
            }
        }
    }

    private val centerEdge by lazy {
        object : TriangleEdgeTreatment(dip(6).toFloat(), false) {
            override fun getEdgePath(length: Float, center: Float, interpolation: Float, shapePath: ShapePath) {
                val centerX = (screenWidth - dip(30)) / 2
                super.getEdgePath(length, centerX.toFloat(), interpolation, shapePath)
            }
        }
    }

    private val endEdge by lazy {
        object : TriangleEdgeTreatment(dip(6).toFloat(), false) {
            override fun getEdgePath(length: Float, center: Float, interpolation: Float, shapePath: ShapePath) {
                val centerX = screenWidth - dip(30) - (screenWidth - dip(74)) / 6
                super.getEdgePath(length, centerX.toFloat(), interpolation, shapePath)
            }
        }
    }

    private fun generateDrawable(direction: Int): Drawable {
        val edge = when (direction) {
            0 -> startEdge
            1 -> centerEdge
            else -> endEdge
        }
        val shapePathModel = ShapeAppearanceModel.builder()
                .setAllCorners(CornerFamily.ROUNDED, dip(8).toFloat())
                .setTopEdge(edge)
                .build()
        return MaterialShapeDrawable(shapePathModel).apply {
            paintStyle = Paint.Style.STROKE
            setStroke(dip(1).toFloat(), Cxt.getColor(R.color.color_e8e8e8))
        }
    }

    private val startDrawable by lazy { generateDrawable(0) }
    private val centerDrawable by lazy { generateDrawable(1) }
    private val endDrawable by lazy { generateDrawable(2) }

    override fun onSetInflaterLayout(): Int = R.layout.dialog_live_evaluation

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        left_text.setOnClickListener {
            sel(3)
        }
        center_text.setOnClickListener {
            sel(2)
        }
        right_text.setOnClickListener {
            sel(1)
        }
        commit_btn.setOnClickListener {
            submit()
        }
        apiSpiceMgr.requestUserInfo(arrayOf(userId)) {
            val userInfo = it.firstOrNull()
            user_avatar_img.glideDisplay(userInfo?.avatar.orEmpty(), userInfo?.gender.genderAvatar())
        }
        back_choice.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }


    private fun submit() {
        commit_btn.isEnabled = false
        val request = EvaluationRequest(EvaluationBean().also {
            it.type = 1
            it.roomId = roomId
            it.ratedUser = userId
            it.grade = nowPos
            it.content = input_evaluation.text.toString().trim()
        })
        apiSpiceMgr.executeToastKt(request, success = {
            if (it.isSuccess) {
                dismissAllowingStateLoss()
                UIToast.toast("提交成功,感谢你的评价")
            }
        }, finish = {
            commit_btn.isEnabled = true
        })

    }

    private var nowPos = 0
    private fun sel(@IntRange(from = 1, to = 3) pos: Int) {
        if (nowPos == pos) return
        resetBtnState()
        when (pos) {
            3 -> {
                left_text.isSelected = true
                left_text.delegate.backgroundColor = Cxt.getColor(R.color.color_FFF8E8)

                input_evaluation.background = startDrawable
            }
            2 -> {
                center_text.isSelected = true
                center_text.delegate.backgroundColor = Cxt.getColor(R.color.color_FFF8E8)

                input_evaluation.background = centerDrawable
            }
            else -> {
                right_text.isSelected = true
                right_text.delegate.backgroundColor = Cxt.getColor(R.color.color_FFF8E8)

                input_evaluation.background = endDrawable
            }
        }
        input_evaluation.isVisible = true
        commit_btn.isVisible = true
        nowPos = pos
        commit_btn.isSelected = true
        commit_btn.isEnabled = true
    }

    private fun resetBtnState() {
        left_text.isSelected = false
        left_text.delegate.backgroundColor = Cxt.getColor(R.color.color_f8f8f8)
        center_text.isSelected = false
        center_text.delegate.backgroundColor = Cxt.getColor(R.color.color_f8f8f8)
        right_text.isSelected = false
        right_text.delegate.backgroundColor = Cxt.getColor(R.color.color_f8f8f8)
    }
}