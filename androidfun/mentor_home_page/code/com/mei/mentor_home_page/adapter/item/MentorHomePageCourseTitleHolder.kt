package com.mei.mentor_home_page.adapter.item

import android.view.View
import android.widget.TextView
import androidx.core.view.isGone
import com.mei.mentor_home_page.model.CourseTitle
import com.mei.wood.R

/**
 * 导师课程title
 * @author Created by lenna on 2020/5/25
 */
class MentorHomePageCourseTitleHolder(view: View) : MentorBaseHolder(view) {

    override fun refresh(item: Any) {
        if (item is CourseTitle) {
            getView<TextView>(R.id.production_title).text = "课程"
            getView<View>(R.id.mentor_home_page_line).isGone = true
        }
    }
}