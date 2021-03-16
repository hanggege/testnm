package com.mei.video.model

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import java.util.*


@SuppressLint("ParcelCreator")
class VideoMediaEntity() : Parcelable {
    var title: String? = null
    var mDuration: String? = null
    var dateTaken: String? = null
    var mimeType: String? = null
    var path: String? = null
    var id: String? = null
    var size: String? = null
    var selected = false
    val mediaType: TYPE
        get() = TYPE.VIDEO

    var duration: String?
        get() = try {
            val duration = mDuration?.toLong() ?: 0
            formatTimeWithMin(duration)
        } catch (e: NumberFormatException) {
            "0:00"
        }
        set(duration) {
            mDuration = duration
        }

    fun formatTimeWithMin(duration: Long): String {
        if (duration <= 0) {
            return String.format(Locale.US, "%02d:%02d", 0, 0)
        }
        val totalSeconds = duration / 1000
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600
        return if (hours > 0) {
            String.format(Locale.US, "%02d:%02d", hours * 60 + minutes,
                    seconds)
        } else {
            String.format(Locale.US, "%02d:%02d", minutes, seconds)
        }
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(title)
        dest?.writeString(mDuration)
        dest?.writeString(dateTaken)
        dest?.writeString(mimeType)
        dest?.writeString(path)
        dest?.writeString(id)
        dest?.writeString(size)
        dest?.writeByte(if (selected) 1 else 0)
    }

    override fun toString(): String {
        return "VideoMediaEntity{" +
                "mTitle='" + title + '\'' +
                ", mDuration='" + mDuration + '\'' +
                ", mDateTaken='" + dateTaken + '\'' +
                ", mMimeType='" + mimeType + '\'' +
                '}'
    }

    val sizeByUnit: String
        get() {
            val sizeString = size.orEmpty()
            val size = java.lang.Double.valueOf(sizeString)
            if (size == 0.0) {
                return "0K"
            }
            if (size >= MB) {
                val sizeInM = size / MB
                return String.format(Locale.getDefault(), "%.1f", sizeInM) + "M"
            }
            val sizeInK = size / 1024
            return String.format(Locale.getDefault(), "%.1f", sizeInK) + "K"
        }

    constructor(parcel: Parcel) : this() {
        title = parcel.readString()
        mDuration = parcel.readString()
        dateTaken = parcel.readString()
        mimeType = parcel.readString()
        path = parcel.readString()
        id = parcel.readString()
        size = parcel.readString()
        selected = parcel.readByte() != 0.toByte()
    }

    val MB = 1024 * 1024.toLong()
    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VideoMediaEntity> {
        override fun createFromParcel(parcel: Parcel): VideoMediaEntity {
            return VideoMediaEntity(parcel)
        }

        override fun newArray(size: Int): Array<VideoMediaEntity?> {
            return arrayOfNulls(size)
        }
    }


}

enum class TYPE {
    IMAGE, VIDEO
}