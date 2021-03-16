package com.mei.login.ext

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.mei.orc.ext.layoutInflaterKt
import com.mei.wood.R
import com.mei.wood.util.AppManager

/**
 *
 * @author Created by Ling on 2019/4/29
 */


fun String.joinCountryCode(countryCode: String) = "${countryCode}-${this}".replace("+", "")


/**
 * 加载图像验证码的图片
 */
fun loadErrBack(url: String?, imageView: ImageView, success: (Boolean) -> Unit = {}) {
    try {
        Glide.with(imageView)
                .load(url.orEmpty())
                .apply(
                        RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                )
                .listener(object : com.bumptech.glide.request.RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        success(false)
                        return false
                    }

                    override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                    ): Boolean {
                        success(true)
                        return false
                    }

                })
                .into(imageView)
    } catch (e: Exception) {
        success(false)
        e.printStackTrace()
    }
}

fun Context.showForbiddenDialog(msg: String, cancelBack: () -> Unit = {}, okBack: () -> Unit = {}) {
    var isClick = false
    val contentView = layoutInflaterKt(R.layout.layout_forbidden_dialog)
    val dialog = AlertDialog.Builder(AppManager.getInstance().currentActivity())
            .setView(contentView)
            .setCancelable(false)
            .setOnDismissListener {
                if (!isClick) cancelBack()
            }
            .create()
    contentView.findViewById<TextView>(R.id.dialog_content).text = msg
    contentView.findViewById<TextView>(R.id.ok_btn).setOnClickListener {
        isClick = true
        dialog.dismiss()
        okBack()
    }
    dialog.show()
}
