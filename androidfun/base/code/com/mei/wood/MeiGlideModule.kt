package com.mei.wood

import android.content.Context
import android.util.Log

import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule

import java.io.InputStream

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/7/20
 * import com.mei.wood.GlideApp
 */
@GlideModule
class MeiGlideModule : AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        registry.prepend(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory())
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)
        val calculator = MemorySizeCalculator.Builder(context)
                .setMemoryCacheScreens(2f)
                .setBitmapPoolScreens(3f)
                .build()
        builder.setMemoryCache(LruResourceCache(calculator.memoryCacheSize.toLong()))
                .setDiskCache(ExternalPreferredCacheDiskCacheFactory(context, (100 * 1024 * 1024).toLong()))//100M
                .setLogLevel(Log.DEBUG)
    }

    override fun isManifestParsingEnabled(): Boolean = false
}
