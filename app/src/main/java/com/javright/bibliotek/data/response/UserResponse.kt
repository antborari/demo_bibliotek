package com.javright.bibliotek.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserResponse(
    var idBook: String = "",
    val email: String = "",
    val phone: String = ""
) : Parcelable {

    fun setIdBook(id: String): UserResponse {
        idBook = id
        return this
    }
}