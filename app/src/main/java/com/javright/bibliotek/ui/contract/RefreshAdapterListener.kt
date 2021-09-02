package com.javright.bibliotek.ui.contract

import com.javright.bibliotek.data.response.UserBookLikesResponse

interface RefreshAdapterListener {
    fun refreshAdapterWithData(userBookLikesResponse: List<UserBookLikesResponse>)

    fun disableLoading()
}