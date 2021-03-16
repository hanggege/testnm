package com.mei.init

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.mei.base.network.holder.SpiceHolder
import com.mei.base.network.holder.SpiceHolderImp
import com.mei.orc.Cxt
import com.mei.orc.john.model.JohnUser
import com.mei.orc.mdid.getMdid
import com.mei.orc.mdid.initMdid
import com.mei.orc.util.app.getProcessName
import com.mei.orc.util.memory.DrawableClear
import kotlin.properties.Delegates


/**
 * Created by guoyufeng on 23/11/2015.
 */
fun meiApplication() = BaseApp.instance

fun spiceHolder() = BaseApp.instance.spiceHolder

abstract class BaseApp : MultiDexApplication() {

    val spiceHolder: SpiceHolder by lazy { SpiceHolderImp() }
    var isMainProcess = false

    companion object {
        var instance: BaseApp by Delegates.notNull()
    }

    @Suppress("UNUSED_VARIABLE")
    override fun attachBaseContext(base: Context) {
        println("attachBaseContext:current:${System.currentTimeMillis()}")
        isMainProcess = isAppProcess(base)
        super.attachBaseContext(base)
        if (isMainProcess) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                Thread({
                    try {
                        initMdid(this)
                        getMdid(this, back = { id ->
                            Log.d("Mdid", id)
                            JohnUser.getSharedUser().phoneSN = id
                            JohnUser.getSharedUser().deviceIMEI = id
                        })
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, "mdid-thread").start()
            }

        }

    }


    override fun onCreate() {
        super.onCreate()
        instance = this
        Cxt.set(this)

        if (isMainProcess) {
            initAll()
            initApp()
        } else {
            DrawableClear.clearPreloadedDrawables(this)
        }


    }


    fun isAppProcess(context: Context) = getProcessName(context) == context.packageName

}
