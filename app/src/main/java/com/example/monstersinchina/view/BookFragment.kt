package com.example.monstersinchina.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.monstersinchina.R
import com.example.monstersinchina.commons.Item
import com.example.monstersinchina.commons.ItemAdapter
import com.example.monstersinchina.commons.ItemManager
import com.example.monstersinchina.service.ViewModel
import com.example.monstersinchina.service.bindNonNull
import com.example.monstersinchina.service.bookLiveData
import kotlinx.android.synthetic.main.fragment_book.view.*

class BookFragment : Fragment() {

    private val viewModel = ViewModel()
    private val itemManager = ItemManager()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_book, container, false)
        view.rv_frag_book.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = ItemAdapter(itemManager)
            itemManager.autoRefresh { removeAll { it is HomeItem } }
        }
        viewModel.getBookList()

        bookLiveData.bindNonNull(this) { bookList ->
            val items = mutableListOf<Item>()
            bookList.forEach { book ->
                items.bookItem {
                    if (book.url.isNullOrEmpty()) {
                        line.visibility = View.GONE
                        name.visibility = View.GONE
                        letter.visibility = View.VISIBLE
                        letter.text = book.text
                    } else {
                        this.itemView.setOnClickListener {
                            Toast.makeText(context, book.url, Toast.LENGTH_SHORT).show()
                        }
                        line.visibility = View.VISIBLE
                        name.visibility = View.VISIBLE
                        letter.visibility = View.GONE
                        name.text = book.text
                    }
                }
            }

            itemManager.autoRefresh {
                removeAll { it is HomeItem }
                addAll(items)
            }

        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = BookFragment()
    }
}
