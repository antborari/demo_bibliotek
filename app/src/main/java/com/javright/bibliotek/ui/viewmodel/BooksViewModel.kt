package com.javright.bibliotek.ui.viewmodel

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import com.javright.bibliotek.BaseApplication
import com.javright.bibliotek.data.model.BookModel
import com.javright.bibliotek.data.request.BookRequest
import com.javright.bibliotek.data.request.UserRequest
import com.javright.bibliotek.data.response.BookResponse
import com.javright.bibliotek.data.response.UserBookLikesResponse
import com.javright.bibliotek.data.response.UserResponse
import com.javright.bibliotek.ui.contract.OnCompleteLikeItem
import com.javright.bibliotek.ui.contract.RefreshAdapterListener
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BooksViewModel @Inject constructor() : ViewModel() {

    private val _scanData: MutableLiveData<List<BookResponse>> =
        MutableLiveData<List<BookResponse>>()
    val scanLiveData: LiveData<List<BookResponse>>
        get() = _scanData

    fun getBooksFirebase(): LiveData<List<BookResponse>> {
        FirebaseDatabase
            .getInstance()
            .reference
            .child("books")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        _scanData.postValue(toBook(dataSnapshot))
                    }
                }
            })
        return scanLiveData
    }

    fun getUserBookLikesFirebase(userId: String, refreshAdapterListener: RefreshAdapterListener) {
        FirebaseDatabase
            .getInstance()
            .reference
            .child("userbooklikes")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    refreshAdapterListener.disableLoading()
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var userBookLikesResponse = listOf<UserBookLikesResponse>()
                    if (dataSnapshot.exists()) {
                        userBookLikesResponse =
                            toUserBookLikes(dataSnapshot).filter { user -> user.idUser == userId }
                    }
                    refreshAdapterListener.refreshAdapterWithData(userBookLikesResponse)
                    refreshAdapterListener.disableLoading()
                }
            })
    }

    fun decomposeText(text: String): List<String> {
        val list = text.split("#")
        if (list.isEmpty())
            return listOf()
        if (list.size != 3)
            return listOf()

        val isbn = list[0]
        val name = list[1]
        val author = list[2]

        return listOf(isbn, name, author)
    }

    fun decomposeAndWriteBookFirebase(
        text: String,
        funcSuccess: (key: String?) -> Unit,
        funcError: () -> Unit
    ) {
        val list = decomposeText(text)
        val book = BookRequest(list[0], list[1], list[2])

        val databaseReference = FirebaseDatabase
            .getInstance()
            .reference
            .child("books/")
            .push()

        databaseReference.setValue(book)
            .addOnSuccessListener {
                funcSuccess(databaseReference.key)
            }
            .addOnFailureListener {
                funcError()
            }
    }

    fun incrementLikeAndWriteUserBookLikesFirebase(
        book: BookModel,
        listenerComplete: OnCompleteLikeItem
    ) {
        listenerComplete.loading(true)

        val postRef: DatabaseReference =
            FirebaseDatabase
                .getInstance()
                .reference
                .child("books/" + book.idBook + "/likes")

        postRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val votes = mutableData.getValue(Int::class.java)
                if (votes == null) {
                    mutableData.value = 1
                } else {
                    mutableData.value = votes + 1
                }
                return Transaction.success(mutableData)
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                success: Boolean,
                dataSnapshot: DataSnapshot?
            ) {
                listenerComplete.loading(false)
            }

        })

        val userBookLikes =
            UserBookLikesResponse(BaseApplication.instance.writeAndGetIdApp(), book.idBook)

        FirebaseDatabase
            .getInstance()
            .reference
            .child("userbooklikes/")
            .push()
            .setValue(userBookLikes)
            .addOnSuccessListener {
                listenerComplete.loading(false)
            }
            .addOnFailureListener {
                listenerComplete.loading(false)
            }
    }

    fun addDataContactFirebase(key: String?, email: Editable?, phone: Editable?) {
        var emailText: String? = null
        var phoneText: String? = null
        email?.let {
            emailText = it.toString()
        }
        phone?.let {
            phoneText = it.toString()
        }

        key?.let {
            if (emailText?.isNotEmpty() == true || phoneText?.isNotEmpty() == true) {
                val user = UserRequest(emailText, phoneText)
                FirebaseDatabase
                    .getInstance()
                    .reference
                    .child("userData/$it")
                    .setValue(user)
            }
        }
    }

    fun getDataContactFirebase(idBook: String?, success: (users: UserResponse) -> Unit, errorUserData : () -> Unit) {
        idBook?.let {
            FirebaseDatabase
                .getInstance()
                .reference
                .child("userData")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            val user = toUser(dataSnapshot).find { x -> x.idBook == it }
                            if (user != null) {
                                success(user)
                            } else {
                                errorUserData()
                            }
                        }
                    }
                })
        }
    }

    private fun toBook(dataSnapshot: DataSnapshot): List<BookResponse> {
        return dataSnapshot.children.map { children ->
            children.getValue(BookResponse::class.java)!!.setIdBook(children.key!!)
        }
    }

    private fun toUserBookLikes(dataSnapshot: DataSnapshot): List<UserBookLikesResponse> {
        return dataSnapshot.children.map { children ->
            children.getValue(UserBookLikesResponse::class.java)!!
        }
    }

    private fun toUser(dataSnapshot: DataSnapshot): List<UserResponse> {
        return dataSnapshot.children.map { children ->
            children.getValue(UserResponse::class.java)!!.setIdBook(children.key!!)
        }
    }
}