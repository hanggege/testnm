package com.mei.wood

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import com.mei.deepLinkMap
import com.mei.orc.http.retrofit.RetrofitClient
import com.mei.orc.john.model.JohnUser
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.json.toJson
import com.mei.orc.util.save.getNonValue
import com.mei.orc.util.save.putValue
import com.mei.provider.AppBuildConfig
import com.mei.wood.ui.MeiCustomBarActivity
import com.mei.wood.util.AppManager
import kotlinx.android.synthetic.main.debug_main.*

/**
 * 佛祖保佑         永无BUG
 * 所有测试入口 ，其它都是二级页面
 *
 * @author Created by joker on 2017/5/8.
 */
class DebugMainActivity : MeiCustomBarActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.debug_main)
        if (!AppBuildConfig.IS_TEST) {
            finish()
        }
        val isTest = isTestClient
        change_client.text = if (isTest) "关闭测试接口环境" else "打开测试接口环境"
        change_client.setBackgroundColor(if (isTest) Color.BLUE else Color.GRAY)
        client_state.text = if (isTest) "你现在在(测试)接口环境" else "你现在在(正式)接口环境"
        client_state.setBackgroundColor(Color.parseColor("#59ca76"))
        if ("current_scheme_is_http".getNonValue(false)) {
            change_http.text = "当前是http请求"
        } else {
            change_http.text = "当前是https请求"
        }
        onClick()
    }

    override fun onResume() {
        super.onResume()
        show_text_tv.text = deepLinkMap.toJson().orEmpty()
    }

    override fun onPause() {
        super.onPause()
        AppManager.getInstance().removeActivity(this)
    }

    fun onClick() {
        change_http.setOnClickListener { }
        change_http.setOnClickListener {
            val isHttp = "current_scheme_is_http".getNonValue(false)
            if (isHttp) {
                "current_scheme_is_http".putValue(false)
                change_http.text = "当前是https请求"
                RetrofitClient.setIsHttpScheme(false)
            } else {
                "current_scheme_is_http".putValue(true)
                change_http.text = "当前是http请求"
                RetrofitClient.setIsHttpScheme(true)
            }
        }
        client_state.setOnClickListener {
            (window.decorView as ViewGroup).getChildAt(1).setBackgroundColor(Color.RED)
            startActivity(Intent(this, ForClientRequestActivity::class.java))
        }
        change_client.setOnClickListener { changeClient() }
        test_method.setOnClickListener {
            startActivity(Intent(this, ForTestMethodActivity::class.java))
        }
        test_login.setOnClickListener {
            if (JohnUser.getSharedUser().hasLogin()) {
                UIToast.toast(this, "已登录")
            } else {
                startActivity(Intent(this, ForTestLoginActivity::class.java))
            }
        }
    }

    private fun changeClient() {
        if (isTestClient) {
            "retrofit_url_is_test".putValue(false)
            RetrofitClient.setIsTestClient(isTestClient)
            change_client.text = "打开测试接口环境"
            change_client.setBackgroundColor(Color.GRAY)
            client_state.text = "你现在在(正式)接口环境"
            Toast.makeText(this, "关闭测试接口环境成功", Toast.LENGTH_SHORT).show()
        } else {
            "retrofit_url_is_test".putValue(true)
            RetrofitClient.setIsTestClient(isTestClient)
            if (isTestClient) {
                change_client.text = "关闭测试接口环境"
                change_client.setBackgroundColor(Color.BLUE)
                client_state.text = "你现在在(测试)接口环境"
                Toast.makeText(this, "打开测试接口环境成功", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "打开测试接口环境失败，请检查是否是beta版", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val isTestClient: Boolean
        get() = AppBuildConfig.IS_TEST && "retrofit_url_is_test".getNonValue(false)
}