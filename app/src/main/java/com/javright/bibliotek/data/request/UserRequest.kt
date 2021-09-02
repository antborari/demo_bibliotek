package com.javright.bibliotek.data.request

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserRequest(val email: String? = null,
                       val phone: String? = null) : Parcelable