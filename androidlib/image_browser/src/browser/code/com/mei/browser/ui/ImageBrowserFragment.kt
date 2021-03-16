package com.mei.browser.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.mei.base.saveImgToGallery
import com.mei.browser.BrowserInfo
import com.mei.browser.adapter.ImageBrowserAdapter
import com.mei.picker.R
import kotlinx.android.synthetic.main.image_browser_layout.*

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/27
 */
class ImageBrowserFragment : DialogFragment() {
    var browserInfo: BrowserInfo = BrowserInfo()

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes = attributes?.apply {
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.MATCH_PARENT
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.image_browser_layout, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        browser_recycler.apply {
            setOnViewPagerListener { _, new, _ ->
                indicator_txt.text = "${new + 1}/${browserInfo.images.size}"
            }
            adapter = ImageBrowserAdapter(browserInfo) { dismissAllowingStateLoss() }
        }
        down_img.visibility = if (browserInfo.downloadable) View.VISIBLE else View.GONE
        indicator_txt.visibility = if (browserInfo.showIndex) View.VISIBLE else View.GONE
        indicator_txt.text = "${browserInfo.index + 1}/${browserInfo.images.size}"
        browser_recycler.scrollToPosition(browserInfo.index)
        down_img.setOnClickListener { activity?.saveImgToGallery(browserInfo.images[browser_recycler.currentPosition]) }
    }

}