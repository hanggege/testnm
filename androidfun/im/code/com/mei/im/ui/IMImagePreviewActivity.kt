package com.mei.im.ui

import android.app.Activity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import com.github.chrisbanes.photoview.PhotoView
import com.mei.chat.ext.EXTRA_IMAGE_FILE_PATH
import com.mei.chat.ext.EXTRA_IMAGE_SEND_ISORIGINAL
import com.mei.orc.ext.getEmptyIntent
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.widget.actionbar.defaultRightTextView
import com.mei.wood.R
import com.mei.wood.extensions.bindView
import com.mei.wood.ui.MeiCustomBarActivity
import java.io.File

class IMImagePreviewActivity : MeiCustomBarActivity() {

    var fileImg: File? = null
    var filePath: String = ""
    val preview_photo_img: PhotoView by bindView(R.id.preview_photo_img)
    val original_img_tv: TextView by bindView(R.id.original_img_tv)
    val select_original: ImageButton by bindView(R.id.select_original)

    private fun readIntent() {
        filePath = intent.getStringExtra(EXTRA_IMAGE_FILE_PATH).orEmpty()
        fileImg = File(filePath)
        fileImg?.let {
            if (!it.exists() || it.length() == 0L) {
                finish()
            }
        }

    }

    @Suppress("UNUSED_PARAMETER")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readIntent()
        setContentView(R.layout.activity_image_preview)

        setCustomView(defaultRightTextView(getString(R.string.submit_send)), 1)
        meiTitleBar.rightContainer.setOnClickListener {
            val intent = getEmptyIntent(Pair(EXTRA_IMAGE_FILE_PATH, filePath), Pair(EXTRA_IMAGE_SEND_ISORIGINAL, select_original.isSelected))
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        preview_photo_img.glideDisplay(fileImg, placeholderId = R.drawable.default_error, errorId = R.drawable.default_error)
        original_img_tv.text = getString(R.string.original_img_size_str, getFileSize(fileImg?.length()
                ?: 0))
        select_original.setOnClickListener { _ -> select_original.isSelected = !select_original.isSelected }

    }


    private fun getFileSize(size: Long): String {
        val strSize = StringBuilder()
        if (size < 1024) {
            strSize.append(size).append("B")
        } else if (size < 1024 * 1024) {
            strSize.append(size / 1024).append("K")
        } else {
            strSize.append(size / 1024 / 1024).append("M")
        }
        return strSize.toString()
    }
}
