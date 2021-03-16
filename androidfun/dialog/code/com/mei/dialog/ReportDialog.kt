package com.mei.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.base.network.holder.SpiceHolder
import com.mei.base.upload.chickUploadAvatar
import com.mei.init.spiceHolder
import com.mei.login.ext.FormatPhoneWatcher
import com.mei.orc.callback.Callback
import com.mei.orc.dialog.BottomDialogFragment
import com.mei.orc.dialog.BottomSelectFragment
import com.mei.orc.dialog.Item
import com.mei.orc.ext.dip
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.keyboard.hideKeyBoard
import com.mei.picker.pickerImage
import com.mei.wood.R
import com.mei.wood.extensions.executeToastKt
import com.net.model.chick.report.ReasonBean
import com.net.model.chick.report.ReportBean
import com.net.network.chick.report.ReportRequest
import kotlinx.android.synthetic.main.view_reportd.*

/**
 *  Created by zzw on 2019-11-29
 *  Des: 举报对话框
 */


fun FragmentActivity?.showReportDialog(userId: Int, roomId: String = "", back: (type: Int) -> Unit = {}) {
    if (this == null) {
        back(0)
        Log.e("ReportDialog", "activity is null")
        return
    }

    if (userId == 0) {
        back(0)
        Log.e("ReportDialog", "userId==0")
        return
    }

    ReportDialog().apply {
        this.userId = userId
        this.roomId = roomId
        this.back = back
    }.show(supportFragmentManager, "ReportDialog")
}

/**
 * 选择需要举报的理由
 */
fun FragmentActivity?.showSelectedReportReason(back: (Item) -> Unit) {
    this?.let {
        val items = getReportReason()
        BottomSelectFragment()
                .setBgColor(ColorDrawable(Color.parseColor("#EFEFEF")))
                .addHeader("选择举报理由", 18.0f, Color.parseColor("#999999"), dip(42), Color.WHITE)
                .setFooter("取消", 18.0f, Color.parseColor("#999999"), height = dip(42), bgColor = Color.WHITE)
                .addItemList<ReasonBean>(items) { _, item -> Item(item.id, item.name, Color.parseColor("#333333"), bgColor = Color.TRANSPARENT) }
                .setItemClickListener(Callback { item -> back(item) })
                .show(it.supportFragmentManager)
    }
}


class ReportDialog : BottomDialogFragment() {

    var userId = 0
    var roomId = ""
    var back: (type: Int) -> Unit = {}

    private val imgList by lazy {
        //占位 add
        arrayListOf("add")
    }

    private val imageAdapter by lazy {
        ImageAdapter(imgList)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.view_reportd, container, false)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        ll_report_why.setOnClickListener {
            activity?.showSelectedReportReason { item ->
                report_why.text = item.display
                resonId = item.type
            }
        }
        report_close.setOnClickListener {
            back(0)
            dismissAllowingStateLoss()
        }

        report_phone_et.addTextChangedListener(object : FormatPhoneWatcher(object : OnFormatPhoneCallback {
            override fun onFormatPhone(phone: String, index: Int) {
                report_phone_et.setText(phone)
                report_phone_et.setSelection(index)
            }
        }) {})


        report_img_recy.layoutManager = GridLayoutManager(context, 5)
        report_img_recy.adapter = imageAdapter

        imageAdapter.setOnItemClickListener { _, _, position ->
            if (position == 0) {
                //add
                activity?.pickerImage(9) {
                    val filePath = it.firstOrNull()
                    if (filePath != null) {
                        val apiClient = (activity as? SpiceHolder)?.apiSpiceMgr
                                ?: spiceHolder().apiSpiceMgr
                        apiClient.chickUploadAvatar(filePath, success = { result ->
                            imgList.add(result.url)
                            imageAdapter.notifyItemInserted(imgList.size - 1)
                        }, failed = {
                            UIToast.toast("发生错误，请重试!")
                        })
                    }
                }
            }
        }

        report_submit.setOnClickListener {
            submit()
        }
        changeSkin()
    }

    private fun changeSkin() {
//        if (checkStyle() == 1) {//直播间
//            report_root.delegate.backgroundColor = Color.parseColor("#E6211B2A")
//            report_title.setTextColor(Color.WHITE)
//            report_close.setLineColor(Color.parseColor("#99FFFFFF"))
//            report_line1.setBackgroundColor(Color.parseColor("#99FFFFFF"))
//            report_why_hint.setTextColor(Color.parseColor("#99FFFFFF"))
//            report_why.setTextColor(Color.WHITE)
//            report_why_arrow.setArrowColor(Color.WHITE)
//            report_des.setTextColor(Color.parseColor("#99FFFFFF"))
//
//            report_content_et.apply {
//                setBackgroundResource(R.drawable.bg_report_edit_dark)
//                setTextColor(Color.WHITE)
//                setHintTextColor(Color.parseColor("#33FFFFFF"))
//            }
//            report_img.setTextColor(Color.parseColor("#99FFFFFF"))
//            report_phone.setTextColor(Color.parseColor("#99FFFFFF"))
//            report_phone_et.apply {
//                setBackgroundResource(R.drawable.bg_report_edit_dark)
//                setTextColor(Color.WHITE)
//                setHintTextColor(Color.parseColor("#33FFFFFF"))
//            }
//
//            report_submit.apply {
//                delegate.backgroundColor = Color.parseColor("#33FFFFFF")
//                setTextColor(Color.WHITE)
//            }
//
//        } else {
        report_root.delegate.backgroundColor = Color.WHITE

        report_title.setTextColor(Color.parseColor("#333333"))

        report_close.setLineColor(Color.parseColor("#989898"))
        report_line1.setBackgroundColor(Color.parseColor("#989898"))
        report_why_hint.setTextColor(Color.parseColor("#989898"))
        report_why.setTextColor(Color.parseColor("#333333"))
        report_why_arrow.setArrowColor(Color.parseColor("#333333"))
        report_des.setTextColor(Color.parseColor("#989898"))
        report_content_et_layout.delegate.strokeColor = ContextCompat.getColor(report_content_et_layout.context, R.color.color_E4E4E4)
        report_content_et.apply {
            setTextColor(Color.parseColor("#333333"))
            setHintTextColor(Color.parseColor("#E4E4E4"))
        }
        report_img.setTextColor(Color.parseColor("#989898"))
        report_phone.setTextColor(Color.parseColor("#989898"))

        report_phone_et_layout.delegate.strokeColor = ContextCompat.getColor(report_content_et_layout.context, R.color.color_E4E4E4)
        report_phone_et.apply {
            setTextColor(Color.parseColor("#333333"))
            setHintTextColor(Color.parseColor("#E4E4E4"))
        }
//        report_submit.apply {
//            delegate.backgroundColor = Color.parseColor("#F8F8F8")
//            setTextColor(Color.parseColor("#989898"))
//        }

//        }
    }


    private var resonId = -1

    private fun submit() {
        if (resonId == -1) {
            UIToast.toast("请选择举报理由")
            return
        }

        val descriptionStr = report_content_et.text.toString().trim()
        if (descriptionStr.isEmpty()) {
            UIToast.toast("请填写举报描述")
            return
        }
        if (descriptionStr.length < 20) {
            UIToast.toast("举报描述不能少于20字")
            return
        }


        val userPhoneStr = report_phone_et.text.toString().trim().replace(" ", "")
        if (userPhoneStr.isEmpty()) {
            UIToast.toast("请填写您的手机号")
            return
        }

        val list = imgList.filter { it != "add" }
        if (list.isEmpty()) {
            UIToast.toast("请上传图片证据")
            return
        }

        report_submit.isEnabled = false
        report_content_et.hideKeyBoard()
        report_content_et.clearFocus()
        val request = ReportRequest(ReportBean().apply {
            reportUser = this@ReportDialog.userId
            roomId = this@ReportDialog.roomId
            reasonId = this@ReportDialog.resonId
            description = descriptionStr
            userPhone = userPhoneStr
            images = list
        })
        val apiClient = (activity as? SpiceHolder)?.apiSpiceMgr ?: spiceHolder().apiSpiceMgr
        apiClient.executeToastKt(request, success = {
            if (it.isSuccess) {
                back(1)
                UIToast.toast("提交成功")
                dismissAllowingStateLoss()
            }
        }, finish = {
            report_submit.isEnabled = true
        })
    }

    class ImageAdapter(list: MutableList<String>) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_report_img, list) {
        override fun convert(holder: BaseViewHolder, item: String) {
            holder.getView<ImageView>(R.id.item_report_iv).apply {
                if (item.isAddAction()) {
//                    if (checkStyle() == 0) {
                    setImageResource(R.drawable.icon_upload_img)
//                    } else {
//                        setImageResource(R.drawable.icon_upload_dark_img)
//                    }
                } else {
                    glideDisplay(item, R.color.color_white_20alpha)
                }
            }

            holder.getView<View>(R.id.item_report_close).apply {
                visibility = if (item.isAddAction()) View.INVISIBLE else View.VISIBLE
                setOnClickListener {
                    val index = data.indexOf(item)
                    data.remove(item)
                    notifyItemRemoved(index)
                }
            }

        }

        private fun String.isAddAction(): Boolean = data.indexOf(this) == 0
    }
}