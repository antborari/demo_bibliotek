package com.javright.bibliotek.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserBookLikesResponse(var idUser: String = "",
                                 val idBook: String = "") : Parcelable