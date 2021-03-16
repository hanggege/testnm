package com.mei.picker.loader

import android.content.ContentResolver
import android.content.Context
import android.os.AsyncTask
import android.provider.MediaStore.Images.Media
import android.util.Log
import com.mei.picker.model.PhotoAlbum
import com.mei.picker.model.PhotoItem

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-09-19
 * 获取本地所有图片
 */
internal class PhotoAlbumLoader(context: Context, val back: (List<PhotoAlbum>) -> Unit) : AsyncTask<Any, Any, Any>() {

    private val resolver: ContentResolver by lazy { context.contentResolver }

    @Suppress("DEPRECATION")
    private fun loadPhotoAlbum(): List<PhotoAlbum> {
        val bucketList = mutableMapOf<String, PhotoAlbum>()
        val columns = arrayOf(Media._ID, Media.BUCKET_ID, Media.DATA, Media.DISPLAY_NAME, Media.TITLE, Media.SIZE, Media.BUCKET_DISPLAY_NAME)
        val cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, columns, null, null, Media.DATE_MODIFIED + " desc")
        cursor?.let { cur ->
            if (cur.moveToFirst()) {
                try {
                    val photoIDIndex = cur.getColumnIndexOrThrow(Media._ID)
                    val photoPathIndex = cur.getColumnIndexOrThrow(Media.DATA)
                    val bucketDisplayNameIndex = cur.getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME)
                    val bucketIdIndex = cur.getColumnIndexOrThrow(Media.BUCKET_ID)
                    /**
                     * Description:这里增加了一个判断：判断照片的名
                     * 字是否合法，例如.jpg .png等没有名字的格式
                     * 如果图片名字是不合法的，直接过滤掉
                     */

                    do {
                        if (cur.getString(photoPathIndex).substring(
                                        cur.getString(photoPathIndex).lastIndexOf("/") + 1,
                                        cur.getString(photoPathIndex).lastIndexOf("."))
                                        .replace(" ", "").isEmpty()) {
                            Log.d("PhotoAlbumLoader", "出现了异常图片的地址：cur.getString(photoPathIndex)=" + cur.getString(photoPathIndex))
                        } else {
                            val _id = cur.getString(photoIDIndex)
                            val path = cur.getString(photoPathIndex)
                            val bucketName = cur.getString(bucketDisplayNameIndex)
                            val bucketId = cur.getString(bucketIdIndex)
                            if (_id.isNotEmpty() && path.isNotEmpty() && bucketName.isNotEmpty() && bucketId.isNotEmpty()) {
                                val bucket = bucketList.getOrPut(bucketId) {
                                    PhotoAlbum()
                                }
                                bucket.bucketName = bucketName
                                bucket.imageList.add(PhotoItem().apply {
                                    imageId = _id
                                    imagePath = path
                                })
                            }

                        }
                    } while (cur.moveToNext())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        cursor?.close()
        return bucketList.map { it.value }
    }

    /**************************[AsyncTask]**************************************/

    override fun doInBackground(vararg params: Any?): Any {
        return loadPhotoAlbum()
    }

    override fun onPostExecute(result: Any?) {
        super.onPostExecute(result)
        @Suppress("UNCHECKED_CAST")
        val list: List<PhotoAlbum> = (result as? List<PhotoAlbum>) ?: arrayListOf()
        back(list)
    }


}