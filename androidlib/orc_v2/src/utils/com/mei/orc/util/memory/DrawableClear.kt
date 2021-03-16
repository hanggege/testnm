package com.mei.orc.util.memory

import android.app.Application
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.util.LongSparseArray

import java.lang.reflect.Field


/**
 * Created by zzw on 2019-10-23
 * Des: 清除由于zygote进程fork而来的预加载资源，可以节约堆内存10M左右，只能在没有ui展示的子进程使用
 */
object DrawableClear {
    private val TAG = "DrawableClear"

    fun clearPreloadedDrawables(application: Application) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val field = getField(Resources::class.java, "mResourcesImpl")
                if (field != null && application.resources != null) {
                    field.isAccessible = true
                    val mResourcesImpl = field.get(application.resources)
                    if (mResourcesImpl != null) {
                        val cls = mResourcesImpl.javaClass
                        Log.d(TAG, cls.simpleName)
                        cleansPreloadedDrawables(cls)
                        cleansPreloadedColorDrawables(cls)
                        cleansPreloadedComplexColors(cls)
                    }
                }
            } else {//min 19
                val cls = Resources::class.java
                cleansPreloadedDrawables(cls)
                cleansPreloadedColorDrawables(cls)
                cleansPreloadedColorStateLists(cls)
            }
            System.gc()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    //24 以上才有
    private fun cleansPreloadedComplexColors(cls: Class<*>) {
        try {
            val dArray = getStaticFieldValue<LongSparseArray<*>?>(cls, "sPreloadedComplexColors")
            dArray?.clear()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    //24以上就没有了
    private fun cleansPreloadedColorStateLists(cls: Class<*>) {
        try {
            val dArray = getStaticFieldValue<LongSparseArray<*>?>(cls, "sPreloadedColorStateLists")
            dArray?.clear()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun cleansPreloadedColorDrawables(cls: Class<*>) {
        try {
            val dArray = getStaticFieldValue<LongSparseArray<*>?>(cls, "sPreloadedColorDrawables")
            dArray?.clear()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun cleansPreloadedDrawables(cls: Class<*>) {
        try {
            val dArray = getStaticFieldValue<Array<LongSparseArray<Drawable.ConstantState?>?>>(cls, "sPreloadedDrawables")
            dArray?.let {
                for (constantStateLongSparseArray in it) {
                    constantStateLongSparseArray?.clear()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun getField(klass: Class<*>, name: String): Field? {
        try {
            return klass.getDeclaredField(name)
        } catch (e: NoSuchFieldException) {
            val parent = klass.superclass ?: return null
            return getField(parent, name)
        }

    }

    private fun <T> getStaticFieldValue(klass: Class<*>?, name: String?): T? {
        if (null != klass && null != name) {
            try {
                val field = getField(klass, name)
                if (null != field) {
                    field.isAccessible = true
                    @Suppress("UNCHECKED_CAST")
                    return field.get(klass) as T
                }
            } catch (t: Throwable) {
                Log.w(TAG, "get field $name of $klass error", t)
            }

        }

        return null
    }
}
