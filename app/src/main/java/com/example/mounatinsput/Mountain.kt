package com.example.mounatinsput

import android.os.Parcel
import android.os.Parcelable

data class Mountain(
    val name: String,
    val mountainImage: String,
    val length: String,
    val description: String,
    val time: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(mountainImage)
        parcel.writeString(length)
        parcel.writeString(description)
        parcel.writeString(time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Mountain> {
        override fun createFromParcel(parcel: Parcel): Mountain {
            return Mountain(parcel)
        }

        override fun newArray(size: Int): Array<Mountain?> {
            return arrayOfNulls(size)
        }
    }
}
