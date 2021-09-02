package com.javright.bibliotek.ui.view

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.javright.bibliotek.R
import com.javright.bibliotek.databinding.ActivitySetBookBinding
import com.javright.bibliotek.ui.viewmodel.BooksViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val EXTRA_ADD = "EXTRA_ADD"

@AndroidEntryPoint
class AddBookActivity : AppCompatActivity() {

    private val mBooksViewModel: BooksViewModel by viewModels()

    private val binding: ActivitySetBookBinding by lazy {
        ActivitySetBookBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val text = intent.getStringExtra(EXTRA_ADD)
        text?.let {
            setViewData(it)
            binding.acceptAddBook.setOnClickListener {
                setBook(text)
            }
        }
        switchDataContact()
    }

    private fun setViewData(text: String) {
        val list = mBooksViewModel.decomposeText(text)
        binding.tvIsbnAddBook.text = list[0]
        binding.tvNameAddBook.text = list[1]
        binding.tvAuthorAddBook.text = list[2]
    }

    private fun setBook(text: String) {
        enableLoading(true)
        mBooksViewModel.decomposeAndWriteBookFirebase(text, ::success, ::error)
    }

    private fun addDataContact(key: String?) {
        mBooksViewModel.addDataContactFirebase(key, binding.tvEmailAddBook.text, binding.tvPhoneAddBook.text)
    }

    private fun success(key: String?) {
        GlobalScope.launch(Dispatchers.Main) {
            val toast =
                Snackbar.make(
                    findViewById(R.id.mainContainer),
                    "Se ha añadido el libro con éxito a Firebase",
                    Snackbar.LENGTH_SHORT
                )
            toast.show()
            delay(2000)
            finish()
            startActivity(Intent(this@AddBookActivity, MainActivity::class.java))
            enableLoading(false)
        }
        if (binding.switchDataContactAddBook.isChecked) {
            addDataContact(key)
        }
    }

    private fun error() {
        val toast =
            Snackbar.make(
                findViewById(R.id.mainContainer),
                "Ha habido un error de conexión al añadir el libro a Firebase",
                Snackbar.LENGTH_SHORT
            )
        toast.show()
        enableLoading(false)
    }

    private fun switchDataContact() {
        binding.switchDataContactAddBook.setOnCheckedChangeListener { _, enable ->
            if (enable) {
                binding.dataContactAddBook.visibility = VISIBLE
            } else {
                binding.dataContactAddBook.visibility = GONE
            }
        }
    }

    private fun enableLoading (enable: Boolean) {
        binding.loadingSetBook.visibility = if (enable) VISIBLE else GONE
    }
}