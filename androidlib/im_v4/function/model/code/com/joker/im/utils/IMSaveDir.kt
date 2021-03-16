package com.joker.im.utils

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Environment
import com.joker.im.provider.imContext
import java.io.File
import java.io.FileOutputStream

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-06-14
 */

internal var SD_CARD_PATH = Environment.getDataDirectory().path

/** im多媒体保存根目录 **/
fun saveRootDir(): String {
    val cacheDir = if (imContext?.checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
        imContext?.getExternalFilesDir(null)?.path ?: ""
    } else {
        imContext?.filesDir?.path ?: ""
    }
    return when {
        cacheDir.isNotEmpty() -> cacheDir
        else -> SD_CARD_PATH
    }
}

fun saveVoiceDir(): File = File("${saveRootDir()}${File.separator}voice").apply { mkdirs() }
fun saveVideoDir(): File = File("${saveRootDir()}${File.separator}video").apply { mkdirs() }
fun saveFileDir(): File = File("${saveRootDir()}${File.separator}files").apply { mkdirs() }

enum class SaveTYPE {
    VOICE, VIDEO, FILE
}

fun getSaveFilePath(type: SaveTYPE, uuid: String): String {
    return when (type) {
        SaveTYPE.VOICE -> "${saveVoiceDir()}${File.separator}$uuid"
        SaveTYPE.VIDEO -> "${saveVideoDir()}${File.separator}$uuid"
        else -> "${saveFileDir()}${File.separator}$uuid"
    }
}


fun Bitmap.saveToLocal(uuid: String): File {
    val file = File(getSaveFilePath(SaveTYPE.FILE, uuid))
    try {
        val out = FileOutputStream(file)
        compress(Bitmap.CompressFormat.PNG, 100, out)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return file
}
