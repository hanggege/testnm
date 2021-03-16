package com.mei.picker.model

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-09-19
 */
internal class PhotoAlbum {
    var bucketName: String = ""
    var imageList: ArrayList<PhotoItem> = arrayListOf()
}

internal class PhotoItem {
    var imageId: String = ""
    var imagePath: String = ""
}