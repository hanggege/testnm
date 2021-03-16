package com.mei.splash.ui

import android.Manifest
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Build.VERSION_CODES.Q
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.webkit.URLUtil
import android.widget.ImageView
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.transition.Transition
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.ChickCustomData
import com.mei.GrowingUtil
import com.mei.base.ui.nav.Nav
import com.mei.checkPrivacy
import com.mei.init.requestTabBar
import com.mei.init.spiceHolder
import com.mei.live.action.IgnoreSystemAction
import com.mei.login.checkFirstLogin
import com.mei.login.quickLogin
import com.mei.login.toLogin
import com.mei.orc.ext.runDelayedOnUiThread
import com.mei.orc.imageload.glide.displayWithCompleteListener
import com.mei.orc.john.model.JohnUser
import com.mei.orc.rxpermission.requestMulPermission
import com.mei.orc.util.encrypt.DigestUtils
import com.mei.orc.util.file.OrcFileUtil
import com.mei.orc.util.image.saveUrlImageAsPNG
import com.mei.orc.util.json.json2Obj
import com.mei.orc.util.save.getNonValue
import com.mei.orc.util.save.getValue
import com.mei.orc.util.save.putValue
import com.mei.orc.util.save.sp
import com.mei.player.view.IgnorePlayerBar
import com.mei.push.Payload
import com.mei.push.imOfflineCustomInfo
import com.mei.push.payLoad
import com.mei.shanyan.realCheckPhoneInfo
import com.mei.showPrivacyDialog
import com.mei.splash.view.SplashView
import com.mei.userinfocomplement.step.ComplementBaseFragment
import com.mei.wood.R
import com.mei.wood.extensions.appLifeTransformer
import com.mei.wood.extensions.executeKt
import com.mei.wood.extensions.subscribeBy
import com.mei.wood.job.checkTestEnvironment
import com.mei.wood.ui.MeiCustomActivity
import com.mei.wood.ui.TabMainActivity
import com.mei.wood.util.AppManager
import com.mei.wood.util.logDebug
import com.net.model.chick.tab.tabbarConfig
import com.net.network.chick.user.MyInfoRequest
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.disposables.Disposable
import java.io.File


class SplashActivity : MeiCustomActivity(), IgnorePlayerBar, IgnoreSystemAction {

    private val SCREEN_IMAGE_URL = "screen_image_url"//存储启动图url
    private val SCREEN_IMAGE_FILE = "screen_image_file"//存储启动图file

    /** GIO的deepLink首次需要等执行完onResume之后才能申请权限，这样才能拿到数据 **/
    private var needRequestPermission = false

    // 首次启动判断是否是测试用户，超时10秒则自动跳转到首页
    private var mainEmitter: ObservableEmitter<Boolean>? = null
    private var dispose: Disposable? = null

    override fun customStatusBar(): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!this.isTaskRoot) {
            intent?.let {
                val action = it.action
                if (it.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN == action) {
                    finish()
                    return
                }
            }
        }
        window.decorView.post { window.decorView.requestFocus() }
        /**个推消息 */
        val payLoadStr = intent.getStringExtra("getuipayload").orEmpty()
        val hasTabMain = AppManager.getInstance().hasActivitys(TabMainActivity::class.java).isNotEmpty()
        if (payLoadStr.isNotEmpty()) {
            payLoadStr.json2Obj<Payload>()?.let {
                /** 这里赋值之后，在可打开的界面去执行弹框或者内链，这里是启动屏，不能操作 **/
                payLoad = it
            }

            /**离线推送消息（腾讯im）主要针对进程被杀死后 数据从
             * @see PushBridgeActivity(在androidlib子项目里所以无法链接跳转)
             * （这里需要注意的是各大厂商手机拿到的数据不一样，出现离线数据无法解析的情况下更换手机进行测试）
             * */
            payLoadStr.json2Obj<ChickCustomData.Result>()?.let {
                //浩哥说这里就处理系统通知的类型就ok，其他的消息类型只要能唤醒app就可以了
                logDebug("getuipayload", it.getType().name)
                if (it.getType() == CustomType.notify || it.getType() == CustomType.quick_apply_exclusive) {
                    imOfflineCustomInfo = it.data
                }
            }
        }

        if (hasTabMain) {
            /** 如果已经打开了App，有跳转处理的话，就处理**/
            if (parseExternalLink()) {
                handleExternal()
            }
            finish()
            return
        } else {
            /** 如果是首次打开，则去到主页再处理跳转与弹框 **/
            parseExternalLink()
            needRequestPermission = true
        }

        checkTestEnvironment(this)
        fullScreen()
        val splashView = SplashView(this)
        showScreenImage(splashView.pictureView)
        setContentView(splashView)
    }

    override fun onResume() {
        super.onResume()
        if (needRequestPermission) {
            needRequestPermission = false
            runDelayedOnUiThread(500) {
                requestPermission()
            }
        }
    }


    fun requestData() {
        println("requestTabBar before:current:${System.currentTimeMillis()}")
        realCheckPhoneInfo()
        if (checkFirstLogin()) {
            dispose = Observable.create(ObservableOnSubscribe<Boolean> { emitter -> mainEmitter = emitter })
                    .appLifeTransformer(this)
                    .subscribeBy {
                        dispose?.dispose()
                        Nav.toTourists(this)
                    }
            runDelayedOnUiThread(10_000) { if (checkPrivacy()) Nav.toTourists(this) }
        }

        requestTabBar { tabBar ->
            preLoadScreenImage(tabBar?.startup.orEmpty())
            println("requestTabBar after:current:${System.currentTimeMillis()}")
            showPrivacyDialog { hasPrivacy ->
                if (hasPrivacy) {
                    launch(tabBar?.guestEnable ?: false)
                } else {
                    finish()
                }
            }
        }
    }

    private fun launch(guestMode: Boolean) {
        val isFirstLaunchLogin = checkFirstLogin()
        when {
            isFirstLaunchLogin && guestMode -> mainEmitter?.onNext(true)
            isFirstLaunchLogin -> quickLogin {
                if (it) toMain() else finish()
            }
            else -> toMain()
        }
    }

    private fun toMain() {
        //如果有登录信息直接到首页
        //如果不是第一次登陆就会到首页
        if (JohnUser.getSharedUser().hasLogin() || !checkFirstLogin()) {

            if (JohnUser.getSharedUser().hasLogin()) {
                spiceHolder().apiSpiceMgr.executeKt(MyInfoRequest(), success = { result ->
                    result.data?.info?.let { info ->
                        info.abVer = tabbarConfig.abVer
                        GrowingUtil.uploadLoginInfo(info)
                    }
                })
            }
            val intent = Intent(this@SplashActivity, TabMainActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        } else {
            //去登录页面
            toLogin { success, _ ->
                if (success) {
                    Nav.toMain(this)
                    finish()
                } else {
                    finish()
                }
            }
        }
    }


    private fun fullScreen() {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        window.decorView.systemUiVisibility = uiOptions
    }

    /**
     * 请求一下权限
     */
    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Q) {
            requestMulPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.READ_PHONE_STATE) {
                requestData()
            }
        } else {
            requestMulPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE) {
                requestData()
            }
        }
    }

    private fun preLoadScreenImage(splashImgUrl: String) {
        val screenImageUrl = SCREEN_IMAGE_URL.getValue("")
        if (splashImgUrl != screenImageUrl && URLUtil.isNetworkUrl(splashImgUrl)) {
            val mImageFile = File(OrcFileUtil.getProtectDir(OrcFileUtil.FileType.image), DigestUtils.md5Hex(splashImgUrl))
            spiceHolder().apiSpiceMgr.saveUrlImageAsPNG(splashImgUrl, mImageFile, success = {
                SCREEN_IMAGE_URL.putValue(splashImgUrl)
                SCREEN_IMAGE_FILE.putValue(it.path)
            })
        } else if (splashImgUrl.isEmpty()) {
            sp.removeValuesForKeys(SCREEN_IMAGE_URL, SCREEN_IMAGE_FILE)
        }
    }

    private fun showScreenImage(imageView: ImageView) {
        val screenImageFile = SCREEN_IMAGE_FILE.getNonValue("")
        if (screenImageFile.isNotEmpty()) {
            //防止为空数据
            displayWithCompleteListener(File(screenImageFile), object : DrawableImageViewTarget(imageView) {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable?>?) {
                    super.onResourceReady(resource, transition)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    imageView.setImageResource(R.drawable.splash_top_bg)
                }
            })
        } else {
            imageView.setImageResource(R.drawable.splash_top_bg)
        }
    }
}