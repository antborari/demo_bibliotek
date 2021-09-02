package com.javright.bibliotek.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.javright.bibliotek.R
import com.javright.bibliotek.data.model.BookModel
import com.javright.bibliotek.databinding.ItemBookBinding

class BookAdapter(
    var myDataSet: List<BookModel>,
    private val onClickItem: (book: BookModel, position: Int) -> Unit,
    private val onLongClickItem: (idBook: String) -> Unit
) : RecyclerView.Adapter<BookAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(myDataSet[position], position, onClickItem, onLongClickItem)
    }

    override fun getItemCount() = myDataSet.size

    class ViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {
        private val binding = ItemBookBinding.bind(view)

        private val tvName = binding.tvName
        private val tvIsbn = binding.tvIsbn
        private val tvVotes = binding.tvVotes
        private val tvAuthor = binding.tvAuthor
        private val like = binding.likeOnEnabled

        fun bind(
            book: BookModel,
            position: Int,
            onClickItem: (book: BookModel, position: Int) -> Unit,
            onLongClickItem: (idBook: String) -> Unit
        ) {
            tvName.text = "Nombre: ${book.name}"
            tvIsbn.text = "ISBN: ${book.isbn}"
            tvVotes.text = "Likes: ${book.likes}"
            tvAuthor.text = "Autor: ${book.author}"
            itemView.setOnClickListener {
                if (!book.disabledOnClickExecute) {
                    onClickItem(book, position)
                }
            }
            if (book.disabledOnClickExecute) {
                like.visibility = VISIBLE
            }
            itemView.setOnLongClickListener {
                onLongClickItem(book.idBook)
                true
            }
        }
    }
}