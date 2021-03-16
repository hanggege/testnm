package com.net.model.chick.friends

import com.mei.orc.http.response.BaseResponse

/**
 * Created by Song Jian
 * Des: 他人主页
 */
class WorkListResult {
    class Response : BaseResponse<WorkListResult?>()
    class WorksBean {
        var hasMore = true
        var nextPageNo = 0
        var worksCount = 0
        var list: ArrayList<ProductBean> = ArrayList()
    }

    var list: WorksBean = WorksBean()
    var count = 0
}