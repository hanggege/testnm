package com.mei.wood

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.core.text.buildSpannedString
import androidx.core.widget.addTextChangedListener
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.gson.reflect.TypeToken
import com.mei.base.ui.nav.Nav
import com.mei.login.checkLoginUserInfo
import com.mei.orc.ext.dip
import com.mei.orc.ext.getIndexOrNull
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.encrypt.DigestUtils
import com.mei.orc.util.json.json2Obj
import com.mei.orc.util.json.toJson
import com.mei.provider.AppBuildConfig
import com.mei.wood.extensions.OkHttpRequest
import com.mei.wood.extensions.appendLink
import com.mei.wood.extensions.executeKt
import com.mei.wood.ui.MeiCustomBarActivity
import com.net.network.chick.login.GetCheckSumRequest
import com.net.network.chick.login.UserAccountPasswordLoginRequest
import kotlinx.android.synthetic.main.for_login_test.*
import kotlinx.coroutines.*
import java.util.*

/**
 * 佛祖保佑         永无BUG
 *
 *
 * Created by joker on 2016/12/26.
 */
// userGroupRoleId 1:房主 2：咨询师 3：分析师
data class TestAccountInfo(val roleId: Int, val userId: Int, val nickname: String,
                           val userGroupId: Int = 0, val userGroupRights: Int = 0, val userGroupRoleId: Int = 0)

class ForTestLoginActivity : MeiCustomBarActivity() {
    private val testAccountUrl = "https://neptune-app.zhixinli.vip/app/testUser/getAllTestUser"
    private val accountAll = arrayListOf<TestAccountInfo>()
    private val accountList = arrayListOf<TestAccountInfo>()

    private val accountAdapter = AccountTestAdapter(accountList).apply {
        setOnItemClickListener { _, _, position ->
            accountList.getIndexOrNull(position)?.let {
                val id = it.userId.toString()
                phone_num.setText(id)
                phone_num.setSelection(id.length)
            }
        }
    }

    private var cachePwdList = LinkedList<String>()

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!AppBuildConfig.DEBUG) {
            return
        }
        setContentView(R.layout.for_login_test)
        account_recycler.adapter = accountAdapter

        account_group.setOnCheckedChangeListener { _, _ ->
            changeAccountList()
        }
        account_group.check(R.id.normal_account)

        apiSpiceMgr.executeKt(OkHttpRequest(testAccountUrl), success = {
            val list = it.data["list"].toJson().orEmpty().json2Obj<List<TestAccountInfo>>(object : TypeToken<List<TestAccountInfo>>() {}.type)
            if (list.orEmpty().isNotEmpty()) accountAll.addAll(list.orEmpty())
            changeAccountList()
        })
        phone_num.addTextChangedListener(afterTextChanged = {
            changeAccountList()
        })
        login_textview.setOnClickListener {
            if (phone_num.text?.toString().orEmpty().isNotEmpty()) {
                cachePwdList.add(password_num.text?.toString().orEmpty())
                cachePwdList.add("1234")
                (0..10).forEach {
                    cachePwdList.add("$it$it$it$it")
                }
                cachePwdList.removeAll { it.isEmpty() }
                looperLogin()
            }
        }
    }

    private fun looperLogin() {
        val pwd = cachePwdList.firstOrNull()
        if (pwd.orEmpty().isNotEmpty()) {
            cachePwdList.removeFirst()
            loginByPWD(phone_num.text?.toString().orEmpty(), pwd.orEmpty()) {
                if (it) {
                    Nav.toMain(this)
                    finish()
                } else {
                    looperLogin()
                }
            }
        } else {
            UIToast.toast(this, "登录失败")
        }
    }

    private fun loginByPWD(phone: String, pwd: String, back: (Boolean) -> Unit) {
        apiSpiceMgr.executeKt(GetCheckSumRequest(phone), success = {
            if (it.isSuccess) {
                val md5Code = DigestUtils.md5Hex(it.data.checksum + DigestUtils.md5Hex(it.data.checksum.substring(0, 4) + DigestUtils.md5Hex("(\$*ngr^@%$pwd")))
                apiSpiceMgr.executeKt(UserAccountPasswordLoginRequest(phone, md5Code, it.data.checksum), success = {
                    if (it.isSuccess) {
                        checkLoginUserInfo(it.data) {
                            back(it)
                        }
                    } else {
                        back(false)
                    }
                }, failure = {
                    back(false)
                })
            } else {
                back(false)
            }
        }, failure = {
            back(false)
        })
    }

    private fun changeAccountList() {
        val isMentor = account_group.checkedRadioButtonId == R.id.vip_account
        val input = phone_num.text?.toString().orEmpty()
        accountList.clear()
        val cacheList = arrayListOf<TestAccountInfo>()
        cacheList.addAll(accountAll.filter { input.isEmpty() || it.userId.toString().contains(input) })
        cacheList.addAll(accountAll.filter { input.isEmpty() || it.nickname.toLowerCase(Locale.getDefault()).contains(input.toLowerCase(Locale.getDefault())) })
        accountList.addAll(cacheList.filter { if (isMentor) it.roleId != 0 else it.roleId == 0 })
        accountAdapter.notifyDataSetChanged()
    }

}

private class AccountTestAdapter(list: MutableList<TestAccountInfo>) : BaseQuickAdapter<TestAccountInfo,
        BaseViewHolder>(android.R.layout.test_list_item, list) {


    override fun convert(holder: BaseViewHolder, item: TestAccountInfo) {
        arrayListOf<String>().groupBy { }
        holder.getView<TextView>(android.R.id.text1).apply {
            setPadding(dip(15), dip(3), dip(15), dip(3))
            text = buildSpannedString {
                val userId = item.userId
                appendLink(item.nickname, Color.parseColor("#333333"))
                appendLink("\n$userId   ：  ", color = Color.parseColor("#FF8420"))
                val roleId = item.roleId
                when {
                    roleId == 2 -> {
                        appendLink("role = $roleId 管理员", Color.RED)
                    }
                    item.userGroupRoleId > 0 -> {
                        appendLink(when (item.userGroupRoleId) {
                            1 -> "工作室- 角色：房主    权限：${item.userGroupRights}"
                            2 -> "工作室- 角色：咨询师  权限：${item.userGroupRights}"
                            3 -> "工作室- 角色：分析师  权限：${item.userGroupRights}"
                            else -> item.userGroupRoleId.toString()
                        })
                    }
                    else -> {
                        appendLink(when (roleId) {
                            0 -> "role = $roleId 用户"
                            1 -> "role = $roleId 知心达人"
                            2 -> "role = $roleId 管理员"
                            3 -> "role = $roleId 助教"
                            else -> roleId.toString()
                        })
                    }
                }
            }
        }
    }

}