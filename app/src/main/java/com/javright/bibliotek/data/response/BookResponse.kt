package com.javright.bibliotek.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookResponse(var idBook: String = "",
                        val name: String = "",
                        val author: String = "",
                        val isbn: String = "",
                        val likes: Int = 0) : Parcelable {

    fun setIdBook(id: String) : BookResponse{
        idBook = id
        return this
    }

}