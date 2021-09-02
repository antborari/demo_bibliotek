package com.javright.bibliotek.ui.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.javright.bibliotek.BaseApplication
import com.javright.bibliotek.data.model.BookModel
import com.javright.bibliotek.data.response.BookResponse
import com.javright.bibliotek.data.response.UserBookLikesResponse
import com.javright.bibliotek.data.response.UserResponse
import com.javright.bibliotek.databinding.ActivityBooksBinding
import com.javright.bibliotek.ui.adapter.BookAdapter
import com.javright.bibliotek.ui.contract.OnCompleteLikeItem
import com.javright.bibliotek.ui.contract.RefreshAdapterListener
import com.javright.bibliotek.ui.viewmodel.BooksViewModel
import com.javright.bibliotek.ui.widget.AlertDialogCustom
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookActivity : AppCompatActivity() {

    private lateinit var mBooksViewModel: BooksViewModel
    private lateinit var books: List<BookModel>
    private val booksAdapter: BookAdapter = BookAdapter(listOf(), ::incrementLike, ::onLongClick)

    private val binding: ActivityBooksBinding by lazy {
        ActivityBooksBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initDashboardViewModel()
        initRecyclerView()
        changeInputFilter()
    }

    private fun initDashboardViewModel() {
        mBooksViewModel = ViewModelProvider(this).get(BooksViewModel::class.java)

        val booksObserver = Observer<List<BookResponse>> { books ->
            val books = books.map { x -> BookModel(x.idBook, x.name, x.author, x.isbn, x.likes) }
            callUserBookLikes(books)
        }

        mBooksViewModel.scanLiveData.observe(this, booksObserver)

        enableLoading(true)
        mBooksViewModel.getBooksFirebase()
    }

    private fun callUserBookLikes(books: List<BookModel>) {
        mBooksViewModel.getUserBookLikesFirebase(
            BaseApplication.instance.writeAndGetIdApp(),
            object : RefreshAdapterListener {
                override fun refreshAdapterWithData(userBookLikesResponse: List<UserBookLikesResponse>) {
                    for (book: BookModel in books) {
                        if (userBookLikesResponse.any { x -> x.idBook == book.idBook }) {
                            book.disabledOnClickExecute = true
                        }
                    }
                    this@BookActivity.books = books
                    refreshAdapter(books)
                }

                override fun disableLoading() {
                    this@BookActivity.enableLoading(false)
                }
            })
    }

    private fun initRecyclerView() {
        binding.recyclerBooks.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = booksAdapter
        }
    }

    private fun refreshAdapter(books: List<BookModel>) {
        booksAdapter.myDataSet = books
        booksAdapter.notifyDataSetChanged()
    }

    private fun incrementLike(book: BookModel, position: Int) {
        mBooksViewModel.incrementLikeAndWriteUserBookLikesFirebase(
            book,
            object : OnCompleteLikeItem {
                override fun loading(enable: Boolean) {
                    enableLoading(enable)
                }
            })
    }

    private fun onLongClick(idBook: String) {
        mBooksViewModel.getDataContactFirebase(idBook, ::successUser, ::errorUserData)
    }

    private fun successUser(user: UserResponse) {
        AlertDialogCustom.newInstance(user).show(supportFragmentManager, BookActivity::class.simpleName)
    }

    private fun errorUserData() {
        Snackbar.make(binding.activityBookView, "No se ha encontrado datos de contacto del libro", Snackbar.LENGTH_SHORT).show()
    }

    private fun changeInputFilter() {
        binding.inputFilter.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                filterSequence(s.toString())
            }
        })
    }

    private fun filterSequence(sequence: String) {
        refreshAdapter(this.books.filter { x -> x.isbn.startsWith(sequence) })
    }

    private fun enableLoading(enable: Boolean) {
        binding.loadingBooks.visibility = if (enable) View.VISIBLE else View.GONE
    }
}