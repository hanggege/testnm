package com.mei.video.loader

import android.content.Context
import android.database.Cursor
import android.os.AsyncTask
import android.provider.MediaStore
import android.provider.MediaStore.Images.ImageColumns
import android.provider.MediaStore.Images.Media
import android.util.Log
import com.mei.video.model.VideoAlbum
import com.mei.video.model.VideoMediaEntity

/**
 * zxj
 * 获取本地所有视频
 */
internal class VideoAlbumLoader(var context: Context, val back: (List<VideoAlbum>) -> Unit) : AsyncTask<Any, Any, Any>() {
    @Suppress("DEPRECATION")
    private fun loadPhotoAlbum(): List<VideoAlbum> {
        val bucketList = mutableMapOf<String, VideoAlbum>()
        val resolver = context.contentResolver
        var cursor: Cursor? = null
        try {
            cursor = resolver?.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(ImageColumns.DATA,
                            MediaStore.Video.Media.TITLE,
                            MediaStore.Video.Media.DISPLAY_NAME,
                            MediaStore.Video.Media.BUCKET_ID,
                            MediaStore.Video.Media._ID,
                            MediaStore.Video.Media.DISPLAY_NAME,
                            MediaStore.Video.Media.RESOLUTION,
                            MediaStore.Video.Media.SIZE,
                            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                            MediaStore.Video.Media.DURATION,
                            MediaStore.Video.Media.DATE_MODIFIED),
                    MediaStore.Video.Media.MIME_TYPE + "=?", arrayOf("video/mp4"), null)
            if (cursor == null)
                return emptyList()
            while (cursor.moveToNext()) {
                // video path
                val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))
                // video id
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID))
                // video display name
                val name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME))
                // video resolution
//                    val resolution = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION))
                // video size
                val size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE))
                //文件所属文件夹
                val bucketName = cursor.getString(cursor.getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME))
                // video duration
                val duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))
                val date = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED))
                val bucketId = cursor.getString(cursor.getColumnIndexOrThrow(Media.BUCKET_ID))
                val bucket = bucketList.getOrPut(bucketId) { VideoAlbum() }
                bucket.bucketName = bucketName
                bucket.videoList.add(VideoMediaEntity().apply {
                    this.id = id.toString()
                    this.path = path
                    this.dateTaken = date.toString()
                    this.duration = duration.toString()
                    this.title = name
                    this.size = size.toString()
                })
                Log.i(bucketName, bucket.videoList.size.toString())
            }
        } finally {
            cursor?.close()
        }
        return bucketList.map { it.value }
    }

    /**************************[AsyncTask]**************************************/
    override fun doInBackground(vararg params: Any?): Any {
        return loadPhotoAlbum()
    }

    override fun onPostExecute(result: Any?) {
        super.onPostExecute(result)
        @Suppress("UNCHECKED_CAST")
        val list: List<VideoAlbum> = (result as? List<VideoAlbum>) ?: arrayListOf()
        back(list)
    }


}