@file:Suppress("UNREAC HABLE_CODE")

package com.mei.me.ext

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.mei.base.common.CHANG_GENDER
import com.mei.base.common.NOTIFY_ME_TAB
import com.mei.base.ui.nav.Nav
import com.mei.base.upload.chickUploadAvatar
import com.mei.dialog.showUploadHeaderDialog
import com.mei.live.manager.genderAvatar
import com.mei.me.activity.CHINESE_PATTERN_COPY
import com.mei.me.activity.PersonalInformationActivity
import com.mei.me.activity.lableCount
import com.mei.me.activity.lastText
import com.mei.orc.event.postAction
import com.mei.orc.ext.addTextChangedListener
import com.mei.orc.ext.layoutInflaterKt
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.orc.util.keyboard.hideKeyBoardDelay
import com.mei.orc.util.keyboard.showKeyBoardDelay
import com.mei.widget.choince.ChoiceView
import com.mei.wood.R
import com.mei.wood.dialog.DISSMISS_DO_NOTHING
import com.mei.wood.dialog.DISSMISS_DO_OK
import com.mei.wood.dialog.DialogData
import com.mei.wood.dialog.NormalDialogLauncher
import com.mei.wood.ext.AmanLink
import com.mei.wood.extensions.executeKt
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.ui.MeiCustomBarActivity
import com.net.MeiUser
import com.net.network.chick.upload.CheckAvatarViolationRequest
import com.net.network.chick.user.EditSkillRequest
import com.net.network.chick.user.UserEditRequest
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import kotlinx.android.synthetic.main.activity_personal_information.*
import java.util.regex.Pattern
import kotlin.math.min

/**
 * Created by hang on 2019-12-12.
 *
 */
fun PersonalInformationActivity.requestSaveData() {
    if (isTrainer && personal_introduction_et_input.text.toString().isEmpty() && labelList.size <= 0) {
        UIToast.toast("请完善自定义标签/个人简介")
        return
    }
    if (isTrainer && personal_introduction_et_input.text.toString().isEmpty()) {
        UIToast.toast("请完善自定义个人简介")
        return
    }

    if (isTrainer && labelList.size <= 0) {
        UIToast.toast("请完善自定义标签")
        return
    }

    if (!isTrainer) {
        val nickname = nick_name.editText.toString()
        if (nickname.isEmpty()) {
            UIToast.toast("请输入您的昵称")
            return
        }
        if (getPatternStr(CHINESE_PATTERN_COPY, nickname) != nickname) {
            UIToast.toast("昵称不能包含特殊字符")
            return
        }
    }

    val editRequest = UserEditRequest().apply {
        //昵称
        nickname = if (MeiUser.getSharedUser().info?.isPublisher == true) {
            MeiUser.getSharedUser().info?.nickname
        } else {
            nick_name.editText.toString()
        }
        //年龄
        birthYear = age_item.editText.toString()
        //性别
        gender = mGenderType
        //感兴趣的内容
        interestId = interestedMap.keys.joinToString(",")

    }
    showLoading(true)
    apiSpiceMgr.executeToastKt(editRequest, success = {
        if (it.isSuccess) {
            postAction(NOTIFY_ME_TAB, true)
            //知心达人擅长领域编辑
            if (isTrainer) {
                saveSkillToNet()
                lastText = ""
            } else {
                UIToast.toast("保存成功")
                setResult(Activity.RESULT_OK, Intent())
                finish()
            }
        } else {
            UIToast.toast(it.errMsg)
        }
    }, failure = {
        showLoading(false)
        UIToast.toast(it?.currMessage)
    }, finish = {
        postAction(CHANG_GENDER, true)
        if (!isTrainer) {
            showLoading(false)
        }
    })
}

fun PersonalInformationActivity.saveSkillToNet() {

    apiSpiceMgr.executeToastKt(EditSkillRequest(skillMap.keys.joinToString(","), personal_introduction_et_input.content.toString(), if (labelList.contains("X")) labelList.dropLast(1).joinToString(",") else labelList.joinToString(",")), success = {
        if (it.isSuccess) {
            UIToast.toast("保存成功")
            postAction(NOTIFY_ME_TAB, true)
            setResult(Activity.RESULT_OK, Intent())
            finish()
        } else {
            UIToast.toast(it.errMsg)
        }
    }, failure = {
        UIToast.toast(it?.currMessage)
    }, finish = {
        showLoading(false)
    })

}


fun EditText.filterPattern(patternStr: String, toastStr: String, maxLength: Int = 8) {
    addTextChangedListener(afterTextChanged = {
        val s = getPatternStr(patternStr, it)
        if (s != it) {
            if (s.isNotEmpty()) {
                setText(s.substring(0, min(maxLength, s.length)))
            } else {
                setText(s)
            }
            setSelection(min(maxLength, s.length))
            UIToast.toast(toastStr)
        }
    })
}

fun getPatternStr(pattern: String, src: String): String {
    var s = ""
    val p = Pattern.compile(pattern)
    src.forEach { char ->
        val m = p.matcher(char.toString())
        if (m.matches()) {
            s += char
        }
    }
    return s
}

/**
 * 上传头像
 */
fun MeiCustomBarActivity.uploadAvatar(photo: String?, back: (String?) -> Unit) {
    if (photo.isNullOrEmpty()) {
        back("")
        return
    }
    apiSpiceMgr.chickUploadAvatar(photo, success = { result ->
        back(result.url)
    }, failed = {
        back("")
    })

}

/**
 * 检测是否有信息修改
 */
fun PersonalInformationActivity.checkChanged(): Boolean {
    val userInfo = MeiUser.getSharedUser().info
    return if (userInfo?.isPublisher == true) {
        val tags = mTagAdapter.datas.joinToString(",")
        val intr = personal_introduction_et_input.content
        userInfo.birthYear != age_item.editText ||
                userInfo.gender != mGenderType ||
                !isEq(userInfo.publisherSkills?.map { it.id }?.toMutableList(), skillMap.keys) ||
                !isEq(userInfo.interests?.map { it.id }?.toMutableList(), interestedMap.keys) ||
                userInfo.introduction != intr || userInfo.tags?.joinToString(",") != tags
    } else {
        userInfo?.nickname != nick_name.editText ||
                userInfo?.birthYear != age_item.editText ||
                userInfo?.gender ?: 0 != mGenderType ||
                !isEq(userInfo?.publisherSkills?.map { it.id }?.toMutableList(), skillMap.keys) ||
                !isEq(userInfo?.interests?.map { it.id }?.toMutableList(), interestedMap.keys)
    }


}

fun isEq(map: MutableList<Int>?, keys: MutableSet<Int>): Boolean {
    return (map.orEmpty().size == keys.size && map.orEmpty().containsAll(keys))

}


class ObserverTextView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0)
    : AppCompatTextView(context, attributeSet, defStyle), TextWatcher {

    private val editTextList = arrayListOf<EditText>()
    private var back: (count: Int) -> Unit = {}

    fun observer(vararg editText: EditText, back: (count: Int) -> Unit = {}) {
        this.back = back
        editTextList.clear()
        editText.forEach {
            it.addTextChangedListener(this)
            editTextList.add(it)
        }
    }

    override fun afterTextChanged(s: Editable?) {
        val count = getHasEditCount(s)
        back(count)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    @Suppress("UNUSED_PARAMETER")
    private fun getHasEditCount(s: Editable?): Int {
        var count = 0
        editTextList.forEach {
            val text = it.text?.toString()?.trim().orEmpty()
            if (text.isNotEmpty()) {
                count++
            }
        }
        return count
    }

}

fun PersonalInformationActivity.uploadHeader() {
    showUploadHeaderDialog { _, extra ->
        showLoading(true)
        uploadAvatar(extra) { url ->
            if (url.isNullOrEmpty()) {
//                UIToast.toast("上传失败")
                showLoading(false)
            } else {
                apiSpiceMgr.executeToastKt(CheckAvatarViolationRequest(url), success = {
                    if (it.isSuccess) {
                        if (it.data?.isViolation == false) {
                            apiSpiceMgr.executeKt(UserEditRequest().apply {
                                avatar = url
                            }, success = { response ->
                                if (response.isSuccess) {
                                    //如果是知心人或者工作室成员，需要运营上传形象照
                                    MeiUser.getSharedUser().info?.avatar = url
                                    if (MeiUser.getSharedUser().info?.isPublisher == true || MeiUser.getSharedUser().info?.groupRole ?: 0 > 0) {
                                        UIToast.toast("请联系运营上传形象照")
                                    } else {
                                        UIToast.toast("上传成功")
                                        image_avatar.glideDisplay(MeiUser.getSharedUser().info?.avatar.orEmpty(), MeiUser.getSharedUser().info?.gender.genderAvatar(MeiUser.getSharedUser().info?.isPublisher))
                                    }
                                } else {
                                    UIToast.toast(response.errMsg)
                                }
                            }, failure = {
                                UIToast.toast(it?.currMessage)
                            }, finish = {
                                showLoading(false)
                            })
                        } else {
                            /**图片违规，重新拉起上传头像弹窗*/
                            UIToast.toast(it.errMsg)
                            uploadHeader()
                        }
                    }

                })
            }
        }
    }
}


/**
 * 标签适配器
 */
class LabelAdapter(var context: Context, var datas: ArrayList<String>, val textDelete: TextView, var txtAdd: TextView) : TagAdapter<String>(datas) {
    override fun getView(parent: FlowLayout?, position: Int, item: String?): View {
        val iv: RelativeLayout = LayoutInflater.from(context).inflate(R.layout.personal_infomation_edit_lable_item, parent, false) as RelativeLayout
        val txtItem: TextView = iv.findViewById(R.id.personal_info_text_label)
        txtItem.text = item
        txtItem.setOnClickListener {
            item?.let { it1 ->
                //不为弹窗提示保存
                NormalDialogLauncher.newInstance().showDialog((context as FragmentActivity), "确定是否删除标签？", back = {
                    when (it) {
                        DISSMISS_DO_OK -> {
                            this.notifyRemove(it1)
                            textDelete.text = String.format(lableCount, (10 - datas.size))
                            txtAdd.isVisible = datas.size < 10
                            UIToast.toast("删除成功")
                        }
                    }
                })

            }
        }
        return iv
    }

    /**
     * 刷新
     */
    fun notify(tag: String) {
        datas.add(tag)
        notifyDataChanged()
    }

    /**
     * 刷新
     */
    private fun notifyRemove(tag: String) {
        datas.remove(tag)
        notifyDataChanged()
    }

}

/**
 * EditText长度和特殊字符的限制
 */
fun setEditTextInhibitInputSpeChat(editText: EditText, textSize: Int) {
    //换行键盘
    val filter = InputFilter { source, _, _, _, _, _ ->
        val speChat = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】_‘；：”“’。，、？]"
        val pattern = Pattern.compile(speChat)
        val matcher = pattern.matcher(source.toString())
        if (matcher.find()) "" else null
    }
    val filterSpace = InputFilter { source, _, _, _, _, _ -> if (source == " ") "" else null }
    editText.filters = arrayOf(filter, filterSpace, InputFilter.LengthFilter(textSize))
}

@SuppressLint("InflateParams")
fun PersonalInformationActivity.showCancelAccountPopWindow(view: View) {
    val popView = LayoutInflater.from(this).inflate(R.layout.home_page_pop_un_account, null)
    val cancel = popView.findViewById<View>(R.id.tv_account_cancel)
    val pop = PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
        setOnDismissListener {
            window.attributes = window.attributes.apply { alpha = 1f }
        }
    }
    cancel.setOnClickListener {
        //注销
        Nav.toWebActivity(this, AmanLink.NetUrl.logout_url)
        window.attributes = window.attributes.apply { alpha = 1f }
        pop.dismiss()
    }

    pop.isOutsideTouchable = true
    pop.isFocusable = true
    pop.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    pop.showAsDropDown(view)

}

@SuppressLint("SetTextI18n")
fun PersonalInformationActivity.showAddLabelDialog() {
    val contentView = layoutInflaterKt(R.layout.dialog_personal_information_lable_add)
    //输入标签
    val etAddInputLabel = contentView.findViewById<EditText>(R.id.et_personal_lable)
    val etChangeLabel = contentView.findViewById<TextView>(R.id.tv_personal_lable_change)
    val dialog = NormalDialogLauncher.newInstance().showDialog(this, DialogData(customView = contentView), back = {
        if (it == DISSMISS_DO_NOTHING) {
            etAddInputLabel.hideKeyBoardDelay()
        }
    })

    setEditTextInhibitInputSpeChat(etAddInputLabel, 8)
    etAddInputLabel.showKeyBoardDelay()
// 然后弹出输入法
    etAddInputLabel.isFocusable = true
    etAddInputLabel.isFocusableInTouchMode = true
    etAddInputLabel.requestFocus()
    etChangeLabel.text = "${etAddInputLabel.text.length}/8"
    contentView.findViewById<ChoiceView>(R.id.choice_view).setOnClickListener {
        dialog.dismissAllowingStateLoss()
    }

    etAddInputLabel.addTextChangedListener {
        etChangeLabel.text = "${it.length}/8"
    }

    contentView.findViewById<TextView>(R.id.tv_dialog_add_lable_commit).setOnClickNotDoubleListener {
        if (labelList.size >= 10) {
            UIToast.toast("最多只能添加10个标签")
            return@setOnClickNotDoubleListener
        }
        if (etAddInputLabel.text.isEmpty()) {
            UIToast.toast("请输入标签")
            return@setOnClickNotDoubleListener
        }
        if (mTagAdapter.datas.contains(etAddInputLabel.text.toString())) {
            UIToast.toast("不能和已有标签重复")
            return@setOnClickNotDoubleListener
        }
        if (etAddInputLabel.text.length < 2) {
            UIToast.toast("标签至少两个字")
            return@setOnClickNotDoubleListener

        }
        UIToast.toast("添加成功")
        mTagAdapter.notify(etAddInputLabel.text.toString())
        personal_lable_count.text = String.format(lableCount, (10 - (mTagAdapter.datas.size)))
        tv_personal_infomation_add_lable.isVisible = mTagAdapter.datas.size < 10
        dialog.dismiss()

    }


}
