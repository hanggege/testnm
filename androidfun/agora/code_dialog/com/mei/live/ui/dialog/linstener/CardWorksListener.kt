package com.mei.live.ui.dialog.linstener

import com.net.model.chick.friends.ProductBean


/**
 *
 * @ProjectName:    dove
 * @Package:        com.mei.live.ui.dialog.linstener
 * @ClassName:      CardWorksListener
 * @Description:
 * @Author:         zxj
 * @CreateDate:     2020/6/11 17:44
 * @UpdateUser:
 * @UpdateDate:     2020/6/11 17:44
 * @UpdateRemark:
 * @Version:
 */

interface CardWorksListener {
    /**
     * 浏览作品
     */
    fun gotoBrowserWorks(item: ProductBean)


}