package com.javright.bibliotek.data.request

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookRequest(val isbn: String = "",
                       val name: String = "",
                       val author: String = "",
                       val likes: Int = 0) : Parcelable