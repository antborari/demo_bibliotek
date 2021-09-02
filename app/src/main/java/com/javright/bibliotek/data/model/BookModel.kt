package com.javright.bibliotek.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookModel(
    var idBook: String = "",
    val name: String = "",
    val author: String = "",
    val isbn: String = "",
    val likes: Int = 0,
    var disabledOnClickExecute: Boolean = false
) : Parcelable