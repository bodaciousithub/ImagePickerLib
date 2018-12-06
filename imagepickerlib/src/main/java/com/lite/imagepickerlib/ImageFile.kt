package com.lite.imagepickerlib

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable

data class ImageFile(var index: Int, val id: String, val title: String, val groupName: String, val path: String, var counter: Int, val isVideo: Int, var checked: Int, var selectionCount: Int = 0) : Parcelable {

    var imageBitmap: Bitmap? = null

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(index)
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(groupName)
        parcel.writeString(path)
        parcel.writeInt(counter)
        parcel.writeInt(isVideo)
        parcel.writeInt(checked)
        parcel.writeInt(selectionCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ImageFile> {
        override fun createFromParcel(parcel: Parcel): ImageFile {
            return ImageFile(parcel)
        }

        override fun newArray(size: Int): Array<ImageFile?> {
            return arrayOfNulls(size)
        }
    }
}